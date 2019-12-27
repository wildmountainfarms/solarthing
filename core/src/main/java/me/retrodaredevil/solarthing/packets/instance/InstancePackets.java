package me.retrodaredevil.solarthing.packets.instance;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.UnknownPacketTypeException;

public final class InstancePackets {
	private InstancePackets(){ throw new UnsupportedOperationException(); }

	@Deprecated
	public static InstancePacket createFromJson(JsonObject jsonObject){
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final InstancePacketType packetType;
		try {
			packetType = InstancePacketType.valueOf(packetName);
		} catch(IllegalArgumentException e){
			throw new UnknownPacketTypeException("packet type name: " + packetName, e);
		}
		switch(packetType){
			case SOURCE:
				return InstanceSourcePackets.createFromJson(jsonObject);
			case TARGET:
				return InstanceTargetPackets.createFromJson(jsonObject);
			case FRAGMENT_INDICATOR:
				return InstanceFragmentIndicatorPackets.createFromJson(jsonObject);
			default:
				throw new UnsupportedOperationException("unimplemented packet type: " + packetType);
		}
	}
}
