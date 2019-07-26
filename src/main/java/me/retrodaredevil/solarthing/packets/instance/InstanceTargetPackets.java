package me.retrodaredevil.solarthing.packets.instance;

import com.google.gson.JsonObject;

public final class InstanceTargetPackets {
	private InstanceTargetPackets(){ throw new UnsupportedOperationException(); }
	public static InstanceTargetPacket create(String targetId){
		return new ImmutableInstanceTargetPacket(targetId);
	}
	public static InstanceTargetPacket createFromJson(JsonObject jsonObject){
		return create(jsonObject.getAsJsonPrimitive("targetId").getAsString());
	}
}
