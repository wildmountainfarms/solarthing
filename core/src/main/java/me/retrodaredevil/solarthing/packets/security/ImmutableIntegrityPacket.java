package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

public final class ImmutableIntegrityPacket implements IntegrityPacket {
	private final String sender;
	private final String encryptedData;

	@JsonCreator
	public ImmutableIntegrityPacket(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "encryptedData", required = true) String encryptedData
	) {
		this.sender = sender;
		this.encryptedData = encryptedData;
	}

	@Override
	public @NotNull String getSender() {
		return sender;
	}

	@Override
	public String getEncryptedData() {
		return encryptedData;
	}

}
