package me.retrodaredevil.solarthing.commands.command;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface OnCommandExecute<T extends Command> {
	void onCommandExecute(SourcedCommand<T> command);
}
