package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class DocumentResponse {
	private final String id;
	private final boolean ok;
	private final String rev;

	@JsonCreator
	public DocumentResponse(
			@JsonProperty("id") String id,
			@JsonProperty("ok") boolean ok,
			@JsonProperty("rev") String rev) {
		requireNonNull(this.id = id);
		this.ok = ok;
		requireNonNull(this.rev = rev);
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("ok")
	public boolean isOk() {
		return ok;
	}

	@JsonProperty("rev")
	public String getRev() {
		return rev;
	}
}
