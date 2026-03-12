package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ImmutableSecurityAcceptPacket implements SecurityAcceptPacket {
	private final String requestDocumentId;

	@JsonCreator
	public ImmutableSecurityAcceptPacket(@JsonProperty(value = "requestDocumentId", required = true) String requestDocumentId) {
		this.requestDocumentId = requireNonNull(requestDocumentId);
	}

	@Override
	public String getRequestDocumentId() {
		return requestDocumentId;
	}
}
