package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
	private final String error;
	private final String reason;

	@JsonCreator
	public ErrorResponse(
			@JsonProperty(value = "error", required = true) String error,
			@JsonProperty(value = "reason", required = true) String reason) {
		this.error = error;
		this.reason = reason;
	}

	@JsonProperty("error")
	public String getError() {
		return error;
	}

	@JsonProperty("reason")
	public String getReason() {
		return reason;
	}
}
