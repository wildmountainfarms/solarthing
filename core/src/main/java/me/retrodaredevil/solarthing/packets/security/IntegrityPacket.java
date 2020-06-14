package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import me.retrodaredevil.solarthing.annotations.NotNull;

/**
 * This type of packet can be used to make sure data is coming from a certain sender.
 */
@JsonTypeName("INTEGRITY_PACKET")
@JsonDeserialize(as = ImmutableIntegrityPacket.class)
@JsonExplicit
public interface IntegrityPacket extends SecurityPacket, SenderPacket {
	@Override
	default @NotNull SecurityPacketType getPacketType(){
		return SecurityPacketType.INTEGRITY_PACKET;
	}

	/**
	 * Should be serialized as "encryptedData"
	 * <p>
	 * When unencrypted it looks like this:
	 * [dateMillis in hex],[data]
	 * @return The data. First encrypted with the sender's private key, then encoded in base64
	 */
	@JsonProperty("encryptedData")
	String getEncryptedData();
}
