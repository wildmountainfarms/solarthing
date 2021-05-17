package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class BulkDocumentResponse {
	private final String id;
	private final Boolean ok;
	private final String revision;
	private final String error;
	private final String reason;


	@JsonCreator
	public BulkDocumentResponse(
			@JsonProperty(value = "id", required = true) String id,
			@JsonProperty("ok") Boolean ok,
			@JsonProperty("rev") String revision,
			@JsonProperty("error") String error,
			@JsonProperty("reason") String reason) {
		requireNonNull(this.id = id);
		this.ok = ok;
		this.revision = revision;
		this.error = error;
		this.reason = reason;
	}

	public String getId() {
		return id;
	}
	public boolean isOk() {
		return Boolean.TRUE.equals(ok);
	}

	public String getRevision() {
		return revision;
	}

	public String getError() {
		return error;
	}

	public String getReason() {
		return reason;
	}
}
