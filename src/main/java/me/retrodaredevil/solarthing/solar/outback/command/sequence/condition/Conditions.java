package me.retrodaredevil.solarthing.solar.outback.command.sequence.condition;

import java.util.Collection;

public final class Conditions {
	private Conditions(){ throw new UnsupportedOperationException(); }
	
	public static final Condition IMMEDIATE;
	static {
		ConditionTask immediateTask = () -> true;
		IMMEDIATE = () -> immediateTask;
	}
	
	/**
	 * Create a condition that doesn't rely on being started. This condition should be able to be called many times
	 * even after {@link ConditionTask#isDone()} returns true
	 * @param conditionTask The condition task that the returned {@link Condition} will always return in {@link Condition#start()}
	 * @return The {@link Condition}
	 */
	public static Condition create(ConditionTask conditionTask){
		return () -> conditionTask;
	}
	
	public static Condition createConditionForAll(Collection<? extends Condition> conditionCollection){
		return new ConditionMultiplexer(conditionCollection, taskList -> {
			for(ConditionTask task : taskList){
				if(!task.isDone()){
					return false;
				}
			}
			return true;
		});
	}
	public static Condition createConditionForAny(Collection<? extends Condition> conditionCollection){
		return new ConditionMultiplexer(conditionCollection, taskList -> {
			for(ConditionTask task : taskList){
				if(task.isDone()){
					return true;
				}
			}
			return false;
		});
	}
}
