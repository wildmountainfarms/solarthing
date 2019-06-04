package me.retrodaredevil.solarthing.solar;

import com.google.gson.JsonObject;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.GregorianCalendar;

import me.retrodaredevil.solarthing.solar.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.solar.mx.MXStatusPackets;
import me.retrodaredevil.util.json.JsonFile;

public final class SolarPackets {
	private SolarPackets(){ throw new UnsupportedOperationException(); }

	public static SolarPacket createFromJson(JsonObject jsonObject) throws IllegalArgumentException{
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final SolarPacketType packetType;
		try {
			packetType = SolarPacketType.valueOf(packetName);
		} catch(IllegalArgumentException e){
			throw new IllegalArgumentException("packet type name: " + packetName, e);
		}
		switch(packetType){
			case FX_STATUS:
				return FXStatusPackets.createFromJson(jsonObject);
			case MXFM_STATUS:
				return MXStatusPackets.createFromJson(jsonObject);
			case FLEXNET_DC_STATUS:
				throw new UnsupportedOperationException("FLEXNet Status Packets aren't supported yet.");
			default:
				throw new UnsupportedOperationException();
		}
	}
	public static void main(String[] args){
		CouchDbClient client = new CouchDbClient(new CouchDbProperties("solarthing", false, "http", "localhost", 5984, "admin", "relax"));
		for(JsonObject object : client.view("packets/millis").startKey(0).query(JsonObject.class)){
			JsonObject value = object.getAsJsonObject("value");
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(value.get("dateMillis").getAsLong());
			PacketCollection packetCollection = PacketCollections.createFromJson(value, SolarPackets::createFromJson);
			System.out.println(JsonFile.gson.toJson(packetCollection));
		}
	}
}
