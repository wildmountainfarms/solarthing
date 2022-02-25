package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

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
		requireNonNull(this.requestDocumentId = requestDocumentId);
		requireNonNull(this.reason = reason);
		requireNonNull(this.moreInfo = moreInfo);
	}

	@Override
	public @NotNull String getRequestDocumentId() {
		return requestDocumentId;
	}

	@Override
	public @NotNull Reason getReason() {
		return reason;
	}

	@Override
	public @NotNull String getMoreInfo() {
		return moreInfo;
	}
}
