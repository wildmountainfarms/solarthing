package me.retrodaredevil.couchdbjava.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(
		getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE,
		creatorVisibility = JsonAutoDetect.Visibility.NONE
)
public class DatabaseSecurity {
	private final SecurityGroup admins;
	private final SecurityGroup members;

	@JsonCreator
	public DatabaseSecurity(@JsonProperty("admins") SecurityGroup admins, @JsonProperty("members") SecurityGroup members) {
		this.admins = admins;
		this.members = members;
	}

	@JsonProperty("admins")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public SecurityGroup getAdmins() {
		return admins;
	}
	public SecurityGroup getAdminsOrBlank() {
		return admins == null ? SecurityGroup.BLANK : admins;
	}

	@JsonProperty("members")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public SecurityGroup getMembers() {
		return members == null ? SecurityGroup.BLANK : members;
	}
}
