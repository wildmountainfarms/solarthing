package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import me.retrodaredevil.solarthing.annotations.NotNull;
import java.security.PublicKey;

@JsonDeserialize(as = ImmutableAuthNewSenderPacket.class)
@JsonTypeName("AUTH_NEW_SENDER")
@JsonExplicit
public interface AuthNewSenderPacket extends SecurityPacket, SenderPacket {
	@NotNull
	@Override
	default SecurityPacketType getPacketType() {
		return SecurityPacketType.AUTH_NEW_SENDER;
	}

	/**
	 * Should be serialized as "publicKey"
	 *
	 * @return The base64 encoded public key
	 */
	@JsonProperty("publicKey")
	String getPublicKey();

	/**
	 * @return The public key that {@link #getPublicKey()} represents
	 */
	PublicKey getPublicKeyObject();
}
