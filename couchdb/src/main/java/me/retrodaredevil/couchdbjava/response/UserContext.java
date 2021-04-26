package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class UserContext {
	private final @Nullable String name;
	private final List<String> roles;

	@JsonCreator
	public UserContext(
			@JsonProperty(value = "name", required = true) @Nullable String name,
			@JsonProperty(value = "roles", required = true) List<String> roles) {
		this.name = name;
		requireNonNull(this.roles = roles);
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
