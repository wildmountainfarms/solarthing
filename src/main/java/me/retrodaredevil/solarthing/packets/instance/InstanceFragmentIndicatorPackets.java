package me.retrodaredevil.solarthing.packets.instance;

import com.google.gson.JsonObject;

public final class InstanceFragmentIndicatorPackets {
	private InstanceFragmentIndicatorPackets(){ throw new UnsupportedOperationException(); }
	
	public static InstanceFragmentIndicatorPacket create(int fragmentId){
		return new ImmutableInstanceFragmentIndicatorPacket(fragmentId);
	}
	public static InstanceFragmentIndicatorPacket createFromJson(JsonObject jsonObject){
		return new ImmutableInstanceFragmentIndicatorPacket(jsonObject.getAsJsonPrimitive("fragmentId").getAsInt());
	}
}
