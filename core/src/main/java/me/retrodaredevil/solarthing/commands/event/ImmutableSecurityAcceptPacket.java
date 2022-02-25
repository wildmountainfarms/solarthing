package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class ImmutableSecurityAcceptPacket implements SecurityAcceptPacket {
	private final String requestDocumentId;

	@JsonCreator
	public ImmutableSecurityAcceptPacket(@JsonProperty(value = "requestDocumentId", required = true) String requestDocumentId) {
		requireNonNull(this.requestDocumentId = requestDocumentId);
	}

	@Override
	public @NotNull String getRequestDocumentId() {
		return requestDocumentId;
	}
}
