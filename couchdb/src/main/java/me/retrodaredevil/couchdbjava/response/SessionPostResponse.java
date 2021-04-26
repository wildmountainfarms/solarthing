package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SessionPostResponse {
	private final boolean ok;
	private final String name;
	private final List<String> roles;

	@JsonCreator
	public SessionPostResponse(
			@JsonProperty(value = "ok", required = true) boolean ok,
			@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "roles", required = true) List<String> roles) {
		this.ok = ok;
		this.name = name;
		this.roles = roles;
	}

	@JsonProperty("ok")
	public boolean isOk() {
		return ok;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("roles")
	public List<String> getRoles() {
		return roles;
	}
}
