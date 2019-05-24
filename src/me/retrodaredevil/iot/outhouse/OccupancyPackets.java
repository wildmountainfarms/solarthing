package me.retrodaredevil.iot.outhouse;

import com.google.gson.JsonObject;

public final class OccupancyPackets {
	private OccupancyPackets(){ throw new UnsupportedOperationException(); }
	
	public static OccupancyPacket createFromJson(JsonObject jsonObject) {
		return new ImmutableOccupancyPacket(jsonObject.getAsJsonPrimitive("occupancy").getAsInt());
	}
}
