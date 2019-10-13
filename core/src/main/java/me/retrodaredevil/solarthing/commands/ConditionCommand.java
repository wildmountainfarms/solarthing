package me.retrodaredevil.solarthing.commands;

import me.retrodaredevil.solarthing.commands.sequence.condition.Condition;

import static java.util.Objects.requireNonNull;

public final class ConditionCommand<T extends Command> {
	
	private final Condition condition;
	private final T command;
	
	/**
	 * @param condition The condition that waits
	 * @param command The command to execute
	 */
	public ConditionCommand(Condition condition, T command) {
		this.condition = requireNonNull(condition);
		this.command = requireNonNull(command);
	}
	
	public Condition getCondition(){
		return condition;
	}
	
	public T getCommand() {
		return command;
	}
}
