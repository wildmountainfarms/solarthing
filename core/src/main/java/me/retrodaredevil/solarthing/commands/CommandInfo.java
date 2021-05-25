package me.retrodaredevil.solarthing.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import static java.util.Objects.requireNonNull;

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
		requireNonNull(this.name = name);
		requireNonNull(this.displayName = displayName);
		requireNonNull(this.description = description);
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
