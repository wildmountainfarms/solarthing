package me.retrodaredevil.solarthing.solar.outback.command.sequence.condition;

/**
 * An immutable representing of a "wait"
 */
public interface Condition {
	ConditionTask start();
}
