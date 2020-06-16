package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonTypeName("LARGE_INTEGRITY_PACKET")
@JsonDeserialize(as = ImmutableLargeIntegrityPacket.class)
@JsonExplicit
public interface LargeIntegrityPacket extends SecurityPacket, SenderPacket {
	@Override
	default @NotNull SecurityPacketType getPacketType(){
		return SecurityPacketType.LARGE_INTEGRITY_PACKET;
	}

	/**
	 * When unencrypted it looks like this:
	 * [dateMillis in hex],[hash (SHA-256)]
	 * <p>
	 * The hash is the SHA-256 hash of {@link #getPayload()} encoded in base64
	 * @return The dateMillis and hash. First encrypted with the sender's private key, then encoded in base64
	 */
	@JsonProperty("encryptedHash")
	String getEncryptedHash();

	/**
	 * @return The payload. Usually JSON data, but not strictly required to be JSON data.
	 */
	@JsonProperty("payload")
	String getPayload();

}
