package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.security.PublicKey;

@JsonDeserialize(as = ImmutableAuthNewSenderPacket.class)
@JsonTypeName("AUTH_NEW_SENDER")
@JsonExplicit
@NullMarked
public interface AuthNewSenderPacket extends SecurityPacket, SenderPacket {
	// TODO remove NonNull
	@DefaultFinal
	@Override
	default @NonNull SecurityPacketType getPacketType() {
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
