package me.retrodaredevil.couchdbjava.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class SecurityGroup {
	public static SecurityGroup BLANK = new SecurityGroup(null, null);

	private final List<String> names;
	private final List<String> roles;

	@JsonCreator
	public SecurityGroup(
			@JsonProperty("names") List<String> names,
			@JsonProperty("roles") List<String> roles) {
		this.names = names == null ? null : Collections.unmodifiableList(names);
		this.roles = roles == null ? null : Collections.unmodifiableList(roles);
	}

	@JsonProperty("names")
	public List<String> getNames() {
		return names;
	}
	public List<String> getNamesOrEmpty() {
		return names == null ? Collections.emptyList() : names;
	}

	@JsonProperty("roles")
	public List<String> getRoles() {
		return roles;
	}
	public List<String> getRolesOrEmpty() {
		return roles == null ? Collections.emptyList() : roles;
	}
}
