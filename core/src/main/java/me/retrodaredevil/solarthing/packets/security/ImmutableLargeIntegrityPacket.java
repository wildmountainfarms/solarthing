package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImmutableLargeIntegrityPacket implements LargeIntegrityPacket {

	private final String sender;
	private final String encryptedHash;
	private final String payload;

	@JsonCreator
	public ImmutableLargeIntegrityPacket(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "encryptedHash", required = true) String encryptedHash,
			@JsonProperty(value = "payload", required = true) String payload) {
		this.sender = sender;
		this.encryptedHash = encryptedHash;
		this.payload = payload;
	}

	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public String getEncryptedHash() {
		return encryptedHash;
	}

	@Override
	public String getPayload() {
		return payload;
	}


}
