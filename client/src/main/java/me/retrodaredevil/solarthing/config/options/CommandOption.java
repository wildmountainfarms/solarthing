package me.retrodaredevil.solarthing.config.options;

import me.retrodaredevil.solarthing.actions.config.ActionReference;
import me.retrodaredevil.solarthing.commands.CommandInfo;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public interface CommandOption {
	@Nullable List<CommandConfig> getDeclaredCommandsNullable();
	default List<CommandConfig> getDeclaredCommands() {
		List<CommandConfig> commandConfigs = getDeclaredCommandsNullable();
		if (commandConfigs == null) {
			return Collections.emptyList();
		}
		return commandConfigs;
	}

	default Map<String, ActionReference> getCommandNameToActionReferenceMap() {
		Map<String, ActionReference> commandFileMap = new HashMap<>();
		for (CommandConfig commandConfig : getDeclaredCommands()) {
			commandFileMap.put(commandConfig.getCommandInfo().getName(), commandConfig.getActionReference());
		}
		return commandFileMap;
	}
	default List<CommandInfo> getCommandInfoList() {
		List<CommandConfig> commandConfigs = getDeclaredCommands();
		List<CommandInfo> r = new ArrayList<>(commandConfigs.size());
		for (CommandConfig commandConfig : commandConfigs) {
			r.add(commandConfig.getCommandInfo());
		}
		return r;
	}
	default boolean hasCommands() {
		return !getDeclaredCommands().isEmpty();
	}
}
