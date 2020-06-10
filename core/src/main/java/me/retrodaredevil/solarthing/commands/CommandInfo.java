package me.retrodaredevil.solarthing.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

@JsonExplicit
public final class CommandInfo {
	private final String name;
	private final String displayName;
	private final String description;

	@JsonCreator
	public CommandInfo(
			@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "displayName", required = true) String displayName,
			@JsonProperty(value = "description", required = true) String description) {
		this.name = name;
		this.displayName = displayName;
		this.description = description;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("displayName")
	public String getDisplayName() {
		return displayName;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}
}
