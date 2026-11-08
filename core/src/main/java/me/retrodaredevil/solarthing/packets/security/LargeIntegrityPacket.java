package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("LARGE_INTEGRITY_PACKET")
@JsonDeserialize(as = ImmutableLargeIntegrityPacket.class)
@JsonExplicit
@NullMarked
public interface LargeIntegrityPacket extends SecurityPacket, SenderPacket {
	@DefaultFinal
	@Override
	default SecurityPacketType getPacketType(){
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
