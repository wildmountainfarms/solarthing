package me.retrodaredevil.solarthing.commands;

public interface OnCommandExecute<T extends Command> {
	void onCommandExecute(SourcedCommand<T> command);
}
