package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.security.PublicKey;

@JsonDeserialize(as = ImmutableAuthNewSenderPacket.class)
@JsonTypeName("AUTH_NEW_SENDER")
public interface AuthNewSenderPacket extends SecurityPacket, SenderPacket {
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
