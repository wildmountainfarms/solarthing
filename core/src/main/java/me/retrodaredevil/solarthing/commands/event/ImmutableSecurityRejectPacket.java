package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public class ImmutableSecurityRejectPacket implements SecurityRejectPacket {
	private final String requestDocumentId;
	private final Reason reason;
	private final String moreInfo;

	@JsonCreator
	public ImmutableSecurityRejectPacket(
			@JsonProperty(value = "requestDocumentId", required = true) String requestDocumentId,
			@JsonProperty(value = "reason", required = true) Reason reason,
			@JsonProperty(value = "moreInfo", required = true) String moreInfo) {
		this.requestDocumentId = requireNonNull(requestDocumentId);
		this.reason = requireNonNull(reason);
		this.moreInfo = requireNonNull(moreInfo);
	}

	@Override
	public @NonNull String getRequestDocumentId() {
		return requestDocumentId;
	}

	@Override
	public @NonNull Reason getReason() {
		return reason;
	}

	@Override
	public @NonNull String getMoreInfo() {
		return moreInfo;
	}
}
