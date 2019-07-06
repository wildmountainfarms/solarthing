package me.retrodaredevil.solarthing.solar.outback.command.sequence.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConditionMultiplexer implements Condition {
	
	private final List<Condition> conditionList;
	private final ConditionMultiplexerIsDone conditionMultiplexerIsDone;
	
	public ConditionMultiplexer(Collection<? extends Condition> conditionCollection, ConditionMultiplexerIsDone conditionMultiplexerIsDone) {
		this.conditionList = Collections.unmodifiableList(new ArrayList<>(conditionCollection));
		this.conditionMultiplexerIsDone = conditionMultiplexerIsDone;
	}
	
	@Override
	public ConditionTask start() {
		return new ConditionMultiplexerTask();
	}
	public interface ConditionMultiplexerIsDone {
		boolean isDone(List<ConditionTask> taskList);
	}
	
	public class ConditionMultiplexerTask implements ConditionTask {
		private final List<ConditionTask> taskList;
		
		public ConditionMultiplexerTask() {
			List<ConditionTask> taskList = new ArrayList<>(conditionList.size());
			for(Condition condition : conditionList){
				taskList.add(condition.start());
			}
			this.taskList = taskList;
		}
		
		@Override
		public boolean isDone() {
			return conditionMultiplexerIsDone.isDone(taskList);
		}
	}
	
}
