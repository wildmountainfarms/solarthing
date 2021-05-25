package me.retrodaredevil.solarthing.config.options;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.commands.CommandInfo;

import java.io.File;
import java.util.*;

public interface CommandOption {
	@Nullable List<CommandConfig> getDeclaredCommandsNullable();
	default @NotNull List<CommandConfig> getDeclaredCommands() {
		List<CommandConfig> commandConfigs = getDeclaredCommandsNullable();
		if (commandConfigs == null) {
			return Collections.emptyList();
		}
		return commandConfigs;
	}

	default Map<String, File> getCommandFileMap() {
		Map<String, File> commandFileMap = new HashMap<>();
		for (CommandConfig commandConfig : getDeclaredCommands()) {
			commandFileMap.put(commandConfig.getName(), commandConfig.getActionFile());
		}
		return commandFileMap;
	}
	default List<CommandInfo> getCommandInfoList() {
		List<CommandConfig> commandConfigs = getDeclaredCommands();
		List<CommandInfo> r = new ArrayList<>(commandConfigs.size());
		for (CommandConfig commandConfig : commandConfigs) {
			r.add(commandConfig.createCommandInfo());
		}
		return r;
	}
	default boolean hasCommands() {
		return !getDeclaredCommands().isEmpty();
	}
}
