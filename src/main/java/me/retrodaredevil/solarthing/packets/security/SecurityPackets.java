package me.retrodaredevil.solarthing.packets.security;

import com.google.gson.JsonObject;

public final class SecurityPackets {
	public static SecurityPacket createFromJson(JsonObject jsonObject){
		final String packetName = jsonObject.getAsJsonPrimitive("packetType").getAsString();
		final SecurityPacketType packetType;
		try {
			packetType = SecurityPacketType.valueOf(packetName);
		} catch(IllegalArgumentException e){
			throw new IllegalArgumentException("packet type name: " + packetName, e);
		}
		switch(packetType){
			case INTEGRITY_PACKET:
			
			case AUTH_NEW_INTEGRITY_SENDER:
				throw new UnsupportedOperationException("I haven't implemented this yet");
			default:
				throw new UnsupportedOperationException();
		}
	}
}
