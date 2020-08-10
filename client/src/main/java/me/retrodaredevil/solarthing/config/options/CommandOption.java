package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.commands.CommandInfo;

import java.io.File;
import java.util.*;

public interface CommandOption {
	@Nullable List<Command> getDeclaredCommandsNullable();
	default @NotNull List<Command> getDeclaredCommands() {
		List<Command> commands = getDeclaredCommandsNullable();
		if (commands == null) {
			return Collections.emptyList();
		}
		return commands;
	}

	default Map<String, File> getCommandFileMap() {
		Map<String, File> commandFileMap = new HashMap<>();
		for (Command command : getDeclaredCommands()) {
			commandFileMap.put(command.name, command.actionFile);
		}
		return commandFileMap;
	}
	default List<CommandInfo> getCommandInfoList() {
		List<Command> commands = getDeclaredCommands();
		List<CommandInfo> r = new ArrayList<>(commands.size());
		for (Command command : commands) {
			r.add(command.createCommandInfo());
		}
		return r;
	}
	default boolean hasCommands() {
		return !getDeclaredCommands().isEmpty();
	}
	class Command {
		@JsonProperty(value = "name", required = true)
		private String name;
		@JsonProperty("display_name")
		private String displayName = null;
		@JsonProperty("description")
		private String description = "";
		@JsonProperty("action")
		private File actionFile;

		private CommandInfo createCommandInfo() {
			return new CommandInfo(name, displayName, description);
		}
	}
}
