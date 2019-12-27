package me.retrodaredevil.solarthing.packets.instance;

import com.google.gson.JsonObject;

public final class InstanceSourcePackets {
	private InstanceSourcePackets(){ throw new UnsupportedOperationException(); }
	public static InstanceSourcePacket create(String sourceId){
		return new ImmutableInstanceSourcePacket(sourceId);
	}
	@Deprecated
	public static InstanceSourcePacket createFromJson(JsonObject jsonObject){
		return create(jsonObject.getAsJsonPrimitive("sourceId").getAsString());
	}
}
