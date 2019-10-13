package me.retrodaredevil.solarthing.commands.sequence.condition;

/**
 * An immutable representing of a "wait"
 */
public interface Condition {
	ConditionTask start();
}
