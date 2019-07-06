package me.retrodaredevil.solarthing.solar.outback.command;

import me.retrodaredevil.solarthing.solar.outback.command.sequence.condition.Condition;

import static java.util.Objects.requireNonNull;

public final class CommandCondition {
	
	private final Condition condition;
	private final MateCommand command;
	
	/**
	 * @param condition The condition that waits
	 * @param command The command to execute
	 */
	public CommandCondition(Condition condition, MateCommand command) {
		this.condition = requireNonNull(condition);
		this.command = requireNonNull(command);
	}
	
	public Condition getCondition(){
		return condition;
	}
	
	public MateCommand getCommand() {
		return command;
	}
}
