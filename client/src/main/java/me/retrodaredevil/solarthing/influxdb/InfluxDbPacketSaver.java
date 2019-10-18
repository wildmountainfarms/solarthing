package me.retrodaredevil.solarthing.influxdb;

import com.google.gson.*;
import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicy;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicyGetter;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicySetting;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * A {@link PacketHandler} that saves packets to InfluxDB.
 * <p>
 * Note that when saving, with the current implementation, integers are not stored. Only floats are stored.
 */
public class InfluxDbPacketSaver implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbPacketSaver.class);
	private static final Logger INFLUX_LOGGER = LoggerFactory.getLogger("org.influxdb");
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();

	private final InfluxProperties properties;
	private final OkHttpProperties okHttpProperties;
	private final DatabaseNameGetter databaseNameGetter;
	private final PacketPointCreator pointCreator;
	private final RetentionPolicyGetter retentionPolicyGetter;

	public InfluxDbPacketSaver(
		InfluxProperties properties,
		OkHttpProperties okHttpProperties,
		DatabaseNameGetter databaseNameGetter,
		PacketPointCreator pointCreator,
		RetentionPolicyGetter retentionPolicyGetter) {
		this.properties = requireNonNull(properties);
		this.okHttpProperties = requireNonNull(okHttpProperties);
		this.databaseNameGetter = requireNonNull(databaseNameGetter);
		this.pointCreator = requireNonNull(pointCreator);
		this.retentionPolicyGetter = retentionPolicyGetter;
	}

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		try(InfluxDB db = createDatabase()) {
			final InstancePacketGroup packetGroup = PacketGroups.parseToInstancePacketGroup(packetCollection);
			final String database = databaseNameGetter.getDatabaseName(packetGroup);
			try {
				QueryResult result = db.query(new Query("CREATE DATABASE " + database));
				String error = getError(result);
				if(error != null){
					throw new PacketHandleException("Result got error! error: " + result);
				}
			} catch (InfluxDBException ex) {
				throw new PacketHandleException("Unable to query the database!", ex);
			}
			final RetentionPolicySetting retentionPolicySetting = retentionPolicyGetter.getRetentionPolicySetting();
			final String retentionPolicyName;

			// region Retention Policy Creation Logic
			if(retentionPolicySetting != null){
				retentionPolicyName = retentionPolicySetting.getName();
				if(retentionPolicyName != null){
					final RetentionPolicy policy = retentionPolicySetting.getRetentionPolicy();
					if(policy != null){
						final String policyString = policy.toPolicyString(retentionPolicyName, database);
						final boolean needsAlter;
						if(retentionPolicySetting.isTryToCreate()){
							final QueryResult result;
							final String query = "CREATE " + policyString;
							try {
								result = db.query(new Query(query));
							} catch(InfluxDBException ex){
								throw new PacketHandleException("Unable to query database to create retention policy: " + retentionPolicyName + " query: " + query, ex);
							}
							String error = getError(result);
							if(retentionPolicySetting.isIgnoreUnsuccessfulCreate()){
								if(error != null){
									LOGGER.debug("We're going to ignore this error we got while trying to create a retention policy. Error: {}", error);
								}
								needsAlter = false;
							} else {
								if(error != null){
									LOGGER.debug("Got error while trying to create! Error: " + error);
								}
								needsAlter = error != null;
							}
							if(needsAlter && !retentionPolicySetting.isAutomaticallyAlter()){
								throw new PacketHandleException("Got error while trying to create retention policy: " + retentionPolicyName + ". Error: " + error);
							}
						} else {
							needsAlter = true;
						}
						if (needsAlter) {
							if (retentionPolicySetting.isAutomaticallyAlter()) {
								final QueryResult alterResult;
								try {
									alterResult = db.query(new Query("ALTER " + policyString));
									LOGGER.info("Successfully altered {} retention policy!", retentionPolicyName);
								} catch (InfluxDBException ex) {
									throw new PacketHandleException("Unable to query database to alter retention policy: " + retentionPolicyName, ex);
								}
								String error = getError(alterResult);
								if (error != null) {
									throw new PacketHandleException("Unable to alter retention policy: " + retentionPolicyName + ". Error: " + error);
								}
							} else {
								throw new PacketHandleException("Retention policy: " + retentionPolicyName + " needs to be altered but automatically alter is false!");
							}
						}
					}
				}
			} else {
				retentionPolicyName = null;
			}
			// endregion

			final long time = packetCollection.getDateMillis();
			final BatchPoints points = BatchPoints.database(database)
				.tag("sourceId", packetGroup.getSourceId())
				.tag("fragmentId", "" + packetGroup.getFragmentId())
				.consistency(InfluxDB.ConsistencyLevel.ALL)
				.retentionPolicy(retentionPolicyName) // may be null, but that's OK
				.build();

			int packetsWritten = 0;
			for (Packet packet : packetGroup.getPackets()) {
				Point.Builder pointBuilder = pointCreator.createBuilder(packet).time(time, TimeUnit.MILLISECONDS);

				JsonObject json = GSON.toJsonTree(packet).getAsJsonObject();
				for (Map.Entry<String, JsonPrimitive> entry : flattenJsonObject(json)) {
					String key = entry.getKey();
					JsonPrimitive prim = entry.getValue();
					if (prim.isNumber()) {
						// always store as float datatype
						Number number = prim.getAsNumber();
						if(number instanceof Float){
							pointBuilder.addField(key, prim.getAsDouble());
						} else {
							/*
							The idea behind this is to use a float instead of a double so if the InfluxDB implementation
							that creates the query treats floats differently than doubles, it may be able to store it more accurately.
							At the time of doing this, I don't think it treats it differently, but might as well have it light this
							because why not.
							 */
							pointBuilder.addField(key, (Number) prim.getAsFloat());
						}
					} else if (prim.isString()) {
						pointBuilder.addField(key, prim.getAsString());
					} else if (prim.isBoolean()) {
						pointBuilder.addField(key, prim.getAsBoolean());
					} else throw new AssertionError("This primitive isn't a number, string or boolean! It's: " + prim);
				}
				points.point(pointBuilder.build());
				packetsWritten++;
			}
			try {
				db.write(points);
			} catch (InfluxDBException ex) {
				throw new PacketHandleException("We were able to query the database, but unable to write the points to it!", ex);
			}
			LOGGER.debug("Wrote {} packets to InfluxDB! database={} retention policy={}", packetsWritten, database, retentionPolicyName);
		}
	}
	private String getError(QueryResult queryResult){
		if(queryResult.hasError()){
			return queryResult.getError();
		}
		List<QueryResult.Result> results = queryResult.getResults();
		for(QueryResult.Result result : results){
			if(result.hasError()){
				return result.getError();
			}
		}
		return null;
	}
	private InfluxDB createDatabase() {
		return InfluxDBFactory.connect(
			properties.getUrl(),
			properties.getUsername(),
			properties.getPassword(),
			new OkHttpClient.Builder()
				.retryOnConnectionFailure(okHttpProperties.isRetryOnConnectionFailure())
				.callTimeout(okHttpProperties.getCallTimeoutMillis(), TimeUnit.MILLISECONDS)
				.connectTimeout(okHttpProperties.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
				.readTimeout(okHttpProperties.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
				.writeTimeout(okHttpProperties.getWriteTimeoutMillis(), TimeUnit.MILLISECONDS)
				.pingInterval(okHttpProperties.getPingIntervalMillis(), TimeUnit.MILLISECONDS)
				.addInterceptor(new HttpLoggingInterceptor(INFLUX_LOGGER::info).setLevel(HttpLoggingInterceptor.Level.BASIC)),
			InfluxDB.ResponseFormat.JSON
		).setLogLevel(InfluxDB.LogLevel.NONE);
	}
	private Set<Map.Entry<String, JsonPrimitive>> flattenJsonObject(JsonObject jsonObject) throws PacketHandleException {
		Map<String, JsonPrimitive> r = new LinkedHashMap<>();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();
			if (element.isJsonPrimitive()) {
				r.put(key, element.getAsJsonPrimitive());
			} else if(element.isJsonObject()){
				Set<Map.Entry<String, JsonPrimitive>> flat = flattenJsonObject(element.getAsJsonObject());
				for(Map.Entry<String, JsonPrimitive> subEntry : flat){
					r.put(key + "." + subEntry.getKey(), subEntry.getValue());
				}
			} else if(!element.isJsonNull()){
				throw new PacketHandleException("This does not support JSON arrays! element: " + element);
			}
		}
		return r.entrySet();
	}
}
