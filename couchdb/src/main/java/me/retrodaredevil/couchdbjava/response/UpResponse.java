package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class UpResponse {
	private final String status;

	@JsonCreator
	public UpResponse(@JsonProperty(value = "status", required = true) String status) {
		requireNonNull(this.status = status);
	}

	public String getStatus() {
		return status;
	}
}
