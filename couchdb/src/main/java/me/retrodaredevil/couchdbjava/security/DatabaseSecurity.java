package me.retrodaredevil.couchdbjava.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseSecurity {
	private final SecurityGroup admins;
	private final SecurityGroup members;

	public DatabaseSecurity(SecurityGroup admins, SecurityGroup members) {
		this.admins = admins;
		this.members = members;
	}

	@JsonProperty("admins")
	public SecurityGroup getAdmins() {
		return admins;
	}
	public SecurityGroup getAdminsOrBlank() {
		return admins == null ? SecurityGroup.BLANK : admins;
	}

	@JsonProperty("members")
	public SecurityGroup getMembers() {
		return members == null ? SecurityGroup.BLANK : members;
	}
}
