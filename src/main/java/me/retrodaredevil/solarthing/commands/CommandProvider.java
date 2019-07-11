package me.retrodaredevil.solarthing.commands;

import me.retrodaredevil.solarthing.commands.source.Source;

public interface CommandProvider<T extends Command> {
	/**
	 * If the returned value is not null, it represents the {@link Command} to execute and the {@link Source} where it came from
	 * @return The {@link SourcedCommand} or null
	 */
	SourcedCommand<T> pollCommand();
}
