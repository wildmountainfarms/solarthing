package me.retrodaredevil.solarthing.commands.command;

public interface OnCommandExecute<T extends Command> {
	void onCommandExecute(SourcedCommand<T> command);
}
