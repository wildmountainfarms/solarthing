package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.UnknownPacketTypeException;

public final class MateCommandFeedbackPackets {
	private MateCommandFeedbackPackets(){ throw new UnsupportedOperationException(); }
	
	public static MateCommandFeedbackPacket createFromJson(JsonObject jsonObject){
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final MateCommandFeedbackPacketType packetType;
		try {
			packetType = MateCommandFeedbackPacketType.valueOf(packetName);
		} catch(IllegalArgumentException e){
			throw new UnknownPacketTypeException("packet type name: " + packetName, e);
		}
		if (packetType == MateCommandFeedbackPacketType.SUCCESS) {
			return SuccessMateCommandPackets.createFromJson(jsonObject);
		}
		throw new UnsupportedOperationException();
	}
}
