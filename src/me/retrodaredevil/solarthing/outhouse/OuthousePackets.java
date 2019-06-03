package me.retrodaredevil.solarthing.outhouse;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.PacketCollection;
import me.retrodaredevil.solarthing.packets.PacketCollections;
import me.retrodaredevil.util.json.JsonFile;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public final class OuthousePackets {
	private OuthousePackets(){ throw new UnsupportedOperationException(); }
	public static OuthousePacket createFromJson(JsonObject jsonObject) throws IllegalArgumentException{
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final OuthousePacketType packetType;
		try {
			packetType = OuthousePacketType.valueOf(packetName);
		} catch(IllegalArgumentException e){
			throw new IllegalArgumentException("packet type name: " + packetName, e);
		}
		switch(packetType){
			case OCCUPANCY:
				return OccupancyPackets.createFromJson(jsonObject);
			case WEATHER:
				return WeatherPackets.createFromJson(jsonObject);
			default:
				throw new UnsupportedOperationException();
		}
	}
	public static void main(String[] args){
		CouchDbClient client = new CouchDbClient(new CouchDbProperties("outhouse", false, "http", "192.168.1.110", 5984, "admin", "relax"));
		for(JsonObject object : client.view("packets/millis").startKey(System.currentTimeMillis() - 20 * 1000).query(JsonObject.class)){
			JsonObject value = object.getAsJsonObject("value");
			PacketCollection packetCollection = PacketCollections.createFromJson(value, OuthousePackets::createFromJson);
			System.out.println(JsonFile.gson.toJson(packetCollection));
		}
		System.out.println("finished successfully");
	}
}
