package me.retrodaredevil.solarthing.influxdb;

import com.google.gson.*;
import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class InfluxDbPacketSaver implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbPacketSaver.class);
	private static final Logger INFLUX_LOGGER = LoggerFactory.getLogger("org.influxdb");
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();

	private final InfluxProperties properties;
	private final OkHttpProperties okHttpProperties;
	private final DatabaseNameGetter databaseNameGetter;
	private final PacketPointCreator pointCreator;

	public InfluxDbPacketSaver(InfluxProperties properties, OkHttpProperties okHttpProperties, DatabaseNameGetter databaseNameGetter, PacketPointCreator pointCreator) {
		this.properties = requireNonNull(properties);
		this.okHttpProperties = requireNonNull(okHttpProperties);
		this.databaseNameGetter = requireNonNull(databaseNameGetter);
		this.pointCreator = requireNonNull(pointCreator);
	}

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		try(InfluxDB db = createDatabase()) {
			InstancePacketGroup packetGroup = PacketGroups.parseToInstancePacketGroup(packetCollection);
			String database = databaseNameGetter.getDatabaseName(packetGroup);
			try {
				db.query(new Query("CREATE DATABASE " + database));
			} catch (InfluxDBException ex) {
				throw new PacketHandleException("Unable to query the database!", ex);
			}
			long time = packetCollection.getDateMillis();
			BatchPoints points = BatchPoints.database(database)
				.tag("sourceId", packetGroup.getSourceId())
				.tag("fragmentId", "" + packetGroup.getFragmentId())
				.consistency(InfluxDB.ConsistencyLevel.ALL)
				.retentionPolicy(null) // TODO
				.build();
			int packetsWritten = 0;
			for (Packet packet : packetGroup.getPackets()) {
				Point.Builder pointBuilder = pointCreator.createBuilder(packet).time(time, TimeUnit.MILLISECONDS);

				JsonObject json = GSON.toJsonTree(packet).getAsJsonObject();
				for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
					String key = entry.getKey();
					JsonElement element = entry.getValue();
					if (element.isJsonPrimitive()) {
						JsonPrimitive prim = element.getAsJsonPrimitive();
						if (prim.isNumber()) {
							pointBuilder.addField(key, prim.getAsNumber());
						} else if (prim.isString()) {
							pointBuilder.addField(key, prim.getAsString());
						} else if (prim.isBoolean()) {
							pointBuilder.addField(key, prim.getAsBoolean());
						} else
							throw new AssertionError("This primitive isn't a number, string or boolean! It's: " + prim);
					} else {
						LOGGER.debug("Key: " + key + " in packet: " + packet + "+ is not a json primitive! string: " + element);
					}
				}
				points.point(pointBuilder.build());
				packetsWritten++;
			}
			try {
				db.write(points);
			} catch (InfluxDBException ex) {
				throw new PacketHandleException("We were able to query the database, but unable to write the points to it!", ex);
			}
			LOGGER.debug("Wrote {} packets to InfluxDB!", packetsWritten);
		}
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

}
