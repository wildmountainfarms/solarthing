package me.retrodaredevil.solarthing.influxdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.annotations.TagKeys;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicy;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicyGetter;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicySetting;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
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
	private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.defaultMapper();

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
	static Collection<String> getTagKeys(Class<?> clazz){
		/*
		Why we have to do this: https://stackoverflow.com/questions/26910620/class-getannotations-getdeclaredannotations-returns-empty-array-for-subcla#26911089
		 */
		Collection<String> tagKeys = new HashSet<>();
		for(Class<?> interfaceClass : clazz.getInterfaces()){
			tagKeys.addAll(getTagKeys(interfaceClass));
		}
		TagKeys[] tagKeysAnnotations = clazz.getAnnotationsByType(TagKeys.class); // since Java 8, but that's fine
		for(TagKeys tagKeysAnnotation : tagKeysAnnotations){
			tagKeys.addAll(Arrays.asList(tagKeysAnnotation.value()));
		}
		return tagKeys;
	}

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		try(InfluxDB db = createDatabase()) {
			final InstancePacketGroup packetGroup = PacketGroups.parseToInstancePacketGroup(packetCollection);
			final String database = databaseNameGetter.getDatabaseName(packetGroup);
			try {
				QueryResult result = db.query(new Query("CREATE DATABASE " + database, null, true));
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
								result = db.query(new Query(query, null, true));
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

				Collection<String> tagKeys = getTagKeys(packet.getClass());
				ObjectNode json = OBJECT_MAPPER.valueToTree(packet);
				for (Map.Entry<String, ValueNode> entry : flattenJsonObject(json)) {
					String key = entry.getKey();
					ValueNode prim = entry.getValue();
					if(tagKeys.contains(key)){
						pointBuilder.tag(key, prim.asText());
					}
					if (prim.isNumber()) {
						// always store as float datatype
						pointBuilder.addField(key, prim.asDouble());
					} else if (prim.isTextual() || prim.isBinary()) {
						pointBuilder.addField(key, prim.asText());
					} else if (prim.isBoolean()) {
						pointBuilder.addField(key, prim.asBoolean());
					} else throw new AssertionError("This primitive isn't a number, string/binary or boolean! It's: " + prim + " class: " + prim.getClass() + " text=" + prim.asText());
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
	private Set<Map.Entry<String, ValueNode>> flattenJsonObject(ObjectNode object) {
		Map<String, ValueNode> r = new LinkedHashMap<>();
		for (Iterator<Map.Entry<String, JsonNode>> it = object.fields(); it.hasNext(); ) {
			Map.Entry<String, JsonNode> entry = it.next();
			String key = entry.getKey();
			JsonNode element = entry.getValue();
			if (element.isValueNode() && !element.isNull()) {
				r.put(key, (ValueNode) element);
			} else if(element.isObject()){
				Set<Map.Entry<String, ValueNode>> flat = flattenJsonObject((ObjectNode) element);
				for(Map.Entry<String, ValueNode> subEntry : flat){
					r.put(key + "." + subEntry.getKey(), subEntry.getValue());
				}
			}
			// ignore nulls and arrays
		}
		return r.entrySet();
	}
}
