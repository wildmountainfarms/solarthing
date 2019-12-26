package me.retrodaredevil.solarthing.packets.security;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.security.crypto.InvalidKeyException;

public final class SecurityPackets {
	@Deprecated
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
				return new ImmutableIntegrityPacket(
					jsonObject.getAsJsonPrimitive("sender").getAsString(),
					jsonObject.getAsJsonPrimitive("encryptedData").getAsString()
				);
			case AUTH_NEW_SENDER:
				try {
					return new ImmutableAuthNewSenderPacket(
						jsonObject.getAsJsonPrimitive("sender").getAsString(),
						jsonObject.getAsJsonPrimitive("publicKey").getAsString()
					);
				} catch (InvalidKeyException e) {
					throw new RuntimeException(e);
				}
			default:
				throw new UnsupportedOperationException();
		}
	}
}
