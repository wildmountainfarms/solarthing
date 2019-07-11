package me.retrodaredevil.solarthing.commands.sequence;

import me.retrodaredevil.solarthing.commands.Command;
import me.retrodaredevil.solarthing.commands.ConditionCommand;
import me.retrodaredevil.solarthing.commands.sequence.condition.Condition;

import java.util.*;

public final class CommandSequence<T extends Command> {
	
	private final List<ConditionCommand<T>> conditionCommandList;
	
	public CommandSequence(Collection<ConditionCommand<T>> conditionCommandCollection) {
		this.conditionCommandList = Collections.unmodifiableList(new ArrayList<>(conditionCommandCollection));
	}
	
	public List<ConditionCommand<T>> getConditionCommandList() {
		return conditionCommandList;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CommandSequence<?> that = (CommandSequence<?>) o;
		return Objects.equals(conditionCommandList, that.conditionCommandList);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(conditionCommandList);
	}
	
	public static final class Builder<T extends Command> {
		private final List<ConditionCommand<T>> conditionCommandList = new ArrayList<>();
		
		public Builder<T> append(ConditionCommand<T> conditionCommand){
			conditionCommandList.add(conditionCommand);
			return this;
		}
		public Builder<T> append(Condition condition, T command){
			return append(new ConditionCommand<>(condition, command));
		}
		public CommandSequence<T> build(){
			return new CommandSequence<>(conditionCommandList);
		}
	}
}
