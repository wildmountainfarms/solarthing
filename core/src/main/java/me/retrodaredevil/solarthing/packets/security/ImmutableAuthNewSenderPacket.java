package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.security.crypto.InvalidKeyException;
import me.retrodaredevil.solarthing.packets.security.crypto.KeyUtil;
import org.jspecify.annotations.NullMarked;

import java.security.PublicKey;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class ImmutableAuthNewSenderPacket implements AuthNewSenderPacket {

	private final String sender;
	private final String publicKey;
	private final PublicKey publicKeyObject;

	@JsonCreator
	public ImmutableAuthNewSenderPacket(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "publicKey", required = true) String publicKey
	) throws InvalidKeyException {
		this.sender = requireNonNull(sender);
		this.publicKey = requireNonNull(publicKey);
		publicKeyObject = KeyUtil.decodePublicKey(publicKey);
	}

	@Override
	public String getPublicKey() {
		return publicKey;
	}

	@Override
	public PublicKey getPublicKeyObject() {
		return publicKeyObject;
	}

	@Override
	public String getSender() {
		return sender;
	}
}
