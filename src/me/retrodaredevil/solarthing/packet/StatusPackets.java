package me.retrodaredevil.solarthing.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.Calendar;
import java.util.GregorianCalendar;

import me.retrodaredevil.solarthing.packet.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.packet.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.packet.mxfm.MXFMStatusPacket;
import me.retrodaredevil.solarthing.packet.mxfm.MXFMStatusPackets;

public final class StatusPackets {
	private StatusPackets(){ throw new UnsupportedOperationException(); }

	public static StatusPacket createFromJson(JsonObject jsonObject) throws IllegalArgumentException{
		final PacketType packetType = PacketType.valueOf(jsonObject.getAsJsonPrimitive("packetType").getAsString());
		switch(packetType){
			case FX_STATUS:
				return FXStatusPackets.createFromJson(jsonObject);
			case MXFM_STATUS:
				return MXFMStatusPackets.createFromJson(jsonObject);
			case FLEXNET_DC_STATUS:
				throw new UnsupportedOperationException("FLEXNet Status Packets aren't supported yet.");
			default:
				throw new UnsupportedOperationException();
		}
	}
	public static <T> T getOrNull(JsonObject jsonObject, String memberName, Getter<T, JsonElement> getter){
		JsonElement element = jsonObject.get(memberName);
		if(element == null){
			return null;
		}
		return getter.get(element);
	}

	/**
	 * Should not be instantiated using an anonymous class, should use a lambda instead.
	 * @param <T> The return value type
	 * @param <H> The input value type
	 */
	public interface Getter<T, H>{
		T get(H h);
	}
	public static void main(String[] args){
		CouchDbClient client = new CouchDbClient(new CouchDbProperties("solarthing", false, "http", "localhost", 5984, "admin", "relax"));
		for(JsonObject object : client.view("packets/millis").startKey(System.currentTimeMillis() - 1000 * 60 * 60).query(JsonObject.class)){
			JsonObject value = object.getAsJsonObject("value");
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(value.get("dateMillis").getAsLong());
			for(JsonElement elementPacket : value.getAsJsonArray("packets")){
				try {
					StatusPacket packet = StatusPackets.createFromJson(elementPacket.getAsJsonObject());
					if(packet.getPacketType() == PacketType.FX_STATUS){
						FXStatusPacket fx = (FXStatusPacket) packet;
						System.out.println("(fx)battery voltage is " + fx.getBatteryVoltageString() + " at " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
					} else if(packet.getPacketType() == PacketType.MXFM_STATUS){
						MXFMStatusPacket mx = (MXFMStatusPacket) packet;
						System.out.println("(mx)battery voltage is " + mx.getBatteryVoltageString() + " at " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
					}
				} catch(NullPointerException ex){
					System.out.println("Couldn't parse:");
					System.out.println(elementPacket.toString());
					throw ex;
				}
			}
		}
	}
}
