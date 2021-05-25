package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.commands.CommandInfo;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class CommandConfig {
	private final String name;
	private final String displayName; // default null
	private final String description; // default empty
	private final File actionFile;

	@JsonCreator
	public CommandConfig(
			@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "display_name", required = true) String displayName,
			@JsonProperty(value = "description", required = true) String description,
			@JsonProperty(value = "action", required = true) File actionFile) {
		requireNonNull(this.name = name);
		requireNonNull(this.displayName = displayName);
		requireNonNull(this.description = description);
		requireNonNull(this.actionFile = actionFile);
	}

	public CommandInfo createCommandInfo() {
		return new CommandInfo(name, displayName, description);
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public File getActionFile() {
		return actionFile;
	}
}
