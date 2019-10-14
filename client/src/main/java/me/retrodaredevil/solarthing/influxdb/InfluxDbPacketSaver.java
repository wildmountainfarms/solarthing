package me.retrodaredevil.solarthing.influxdb;

import com.google.gson.*;
import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.influxdb.InfluxDB;
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
	private final String database;

	public InfluxDbPacketSaver(InfluxProperties properties, String database) {
		this.properties = requireNonNull(properties);
		this.database = requireNonNull(database);
	}

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		/*
		This piece of code uses the asynchronous features of the influxdb-java library. Because of this, PacketHandlerExceptions are
		not thrown. We will just log errors.
		 */
//		InfluxDB db = InfluxDBFactory.connect("http://localhost:8086", "root", "root", new OkHttpClient.Builder(), InfluxDB.ResponseFormat.JSON);
		InfluxDB db = InfluxDBFactory.connect(
			properties.getUrl(),
			properties.getUsername(),
			properties.getPassword(),
			new OkHttpClient.Builder()
				.retryOnConnectionFailure(properties.isRetryOnConnectionFailure())
				.callTimeout(properties.getCallTimeoutMillis(), TimeUnit.MILLISECONDS)
				.connectTimeout(properties.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
				.readTimeout(properties.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
				.writeTimeout(properties.getWriteTimeoutMillis(), TimeUnit.MILLISECONDS)
				.pingInterval(properties.getPingIntervalMillis(), TimeUnit.MILLISECONDS)
				.addInterceptor(new HttpLoggingInterceptor(INFLUX_LOGGER::debug).setLevel(HttpLoggingInterceptor.Level.BASIC)),
			InfluxDB.ResponseFormat.JSON
		);
		db.setLogLevel(InfluxDB.LogLevel.NONE); // we have our own way of doing this
//		db.enableBatch(BatchOptions.DEFAULTS.exceptionHandler(((points, throwable) -> {
//
//		})));
		db.query(new Query("CREATE DATABASE " + database));
		long time = packetCollection.getDateMillis();
		InstancePacketGroup packetGroup = PacketGroups.parseToInstancePacketGroup(packetCollection);
		BatchPoints points = BatchPoints.database(database)
			.tag("sourceId", packetGroup.getSourceId())
			.tag("fragmentId", "" + packetGroup.getFragmentId())
			.consistency(InfluxDB.ConsistencyLevel.ALL)
			.build();
		int packetsWritten = 0;
		for(Packet packet : packetGroup.getPackets()){
			final Point.Builder pointBuilder;
			String debugMeasurement = null;
			if(packet instanceof DocumentedPacket){
				DocumentedPacket<? extends DocumentedPacketType> documentedPacket = (DocumentedPacket<? extends DocumentedPacketType>) packet;
				DocumentedPacketType type = documentedPacket.getPacketType();
				debugMeasurement = type.toString();
				if(packet instanceof Identifiable){
					Identifier identifier = ((Identifiable) packet).getIdentifier();
					pointBuilder = Point.measurement(type.toString()).tag("identifier", identifier.getRepresentation());
				} else {
					pointBuilder = Point.measurement(type.toString());
				}
			} else {
				debugMeasurement = packet.getClass().getSimpleName();
				pointBuilder = Point.measurement(packet.getClass().getSimpleName());
			}
			pointBuilder.time(time, TimeUnit.MILLISECONDS);

			JsonObject json = GSON.toJsonTree(packet).getAsJsonObject();
			for(Map.Entry<String, JsonElement> entry : json.entrySet()){
				String key = entry.getKey();
				JsonElement element = entry.getValue();
				if(element.isJsonPrimitive()){
					JsonPrimitive prim = element.getAsJsonPrimitive();
					if(prim.isNumber()){
						pointBuilder.addField(key, prim.getAsNumber());
					} else if(prim.isString()){
						pointBuilder.addField(key, prim.getAsString());
					} else if(prim.isBoolean()){
						pointBuilder.addField(key, prim.getAsBoolean());
					} else throw new AssertionError("This primitive isn't a number, string or boolean! It's: " + prim);
				} else {
					LOGGER.debug("Key: " + key + " in measurement: " + debugMeasurement + " is not a json primitive! string: " + element);
				}
			}
			points.point(pointBuilder.build());
			packetsWritten++;
		}
		db.write(points);
		LOGGER.debug("Wrote {} packets to InfluxDB!", packetsWritten);
	}
}
