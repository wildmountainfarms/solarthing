package me.retrodaredevil.solarthing.commands.command;


public interface CommandProvider<T extends Command> {
	/**
	 * If the returned value is not null, it represents the {@link Command} to execute and the source where it came from
	 * @return The {@link SourcedCommand} or null
	 */
	SourcedCommand<T> pollCommand();
}
