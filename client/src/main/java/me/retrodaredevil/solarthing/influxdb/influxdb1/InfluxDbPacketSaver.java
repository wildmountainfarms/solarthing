package me.retrodaredevil.solarthing.influxdb.influxdb1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import me.retrodaredevil.influxdb.influxdb1.InfluxProperties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.okhttp3.OkHttpUtil;
import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.influxdb.NameGetter;
import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicy;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicyGetter;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicySetting;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
	private final NameGetter databaseNameGetter;
	private final PacketPointCreator pointCreator;
	private final RetentionPolicyGetter retentionPolicyGetter;

	public InfluxDbPacketSaver(
			InfluxProperties properties,
			OkHttpProperties okHttpProperties,
			NameGetter databaseNameGetter,
			PacketPointCreator pointCreator,
			RetentionPolicyGetter retentionPolicyGetter) {
		this.properties = requireNonNull(properties);
		this.okHttpProperties = requireNonNull(okHttpProperties);
		this.databaseNameGetter = requireNonNull(databaseNameGetter);
		this.pointCreator = requireNonNull(pointCreator);
		this.retentionPolicyGetter = retentionPolicyGetter;
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
		try(InfluxDB db = createDatabase()) {
			final InstancePacketGroup packetGroup = PacketGroups.parseToInstancePacketGroup(packetCollection, DefaultInstanceOptions.REQUIRE_NO_DEFAULTS);
			DefaultInstanceOptions.requireNoDefaults(packetGroup);
			final String database = databaseNameGetter.getName(packetGroup);
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
						final String policyString = policy.toPolicyStringInfluxDb1(retentionPolicyName, database);
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
									LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Successfully altered {} retention policy!", retentionPolicyName);
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

				Collection<String> tagKeys = PointUtil.getTagKeys(packet.getClass());
				ObjectNode json = OBJECT_MAPPER.valueToTree(packet);
				for (Map.Entry<String, ValueNode> entry : PointUtil.flattenJsonObject(json)) {
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
				OkHttpUtil.createBuilder(okHttpProperties)
						.addInterceptor(new HttpLoggingInterceptor(INFLUX_LOGGER::info).setLevel(HttpLoggingInterceptor.Level.BASIC)),
				InfluxDB.ResponseFormat.JSON
		).setLogLevel(InfluxDB.LogLevel.NONE);
	}
}
