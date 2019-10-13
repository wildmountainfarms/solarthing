package me.retrodaredevil.solarthing.influxdb;

import com.google.gson.*;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IntegerIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfluxDbPacketSaver implements PacketHandler {
	private static final Logger LOGGER = LogManager.getLogger(InfluxDbPacketSaver.class);
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		/*
		This piece of code uses the asynchronous features of the influxdb-java library. Because of this, PacketHandlerExceptions are
		not thrown. We will just log errors.
		 */
		InfluxDB db = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
//		db.enableBatch(BatchOptions.DEFAULTS.exceptionHandler(((points, throwable) -> {
//
//		})));
		String database = "solar_data";
		db.query(new Query("CREATE DATABASE " + database));
		long time = packetCollection.getDateMillis();
		BatchPoints points = BatchPoints.database(database).build();
		int packetsWritten = 0;
		for(Packet packet : packetCollection.getPackets()){
			final Point.Builder pointBuilder;
			String debugMeasurement = null;
			if(packet instanceof DocumentedPacket){
				DocumentedPacket<? extends DocumentedPacketType> documentedPacket = (DocumentedPacket<? extends DocumentedPacketType>) packet;
				DocumentedPacketType type = documentedPacket.getPacketType();
				debugMeasurement = type.toString();
				if(packet instanceof Identifiable){
					Identifier identifier = ((Identifiable) packet).getIdentifier();
					if(identifier instanceof IntegerIdentifier){ // TODO make identifiers implement toString
						pointBuilder = Point.measurement(type.toString()).tag("identifier", ((IntegerIdentifier) identifier).getIntegerIdentifier() + "");
					} else {
						pointBuilder = Point.measurement(type.toString()).tag("identifier", identifier.toString());
					}
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
