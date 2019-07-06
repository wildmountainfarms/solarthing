package me.retrodaredevil.solarthing.solar.outback.command.sequence;

import me.retrodaredevil.solarthing.solar.outback.command.CommandCondition;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.condition.Condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CommandSequence {
	
	private final List<CommandCondition> commandConditionList;
	
	public CommandSequence(Collection<CommandCondition> commandConditionCollection) {
		this.commandConditionList = Collections.unmodifiableList(new ArrayList<>(commandConditionCollection));
	}
	
	public List<CommandCondition> getCommandConditionList() {
		return commandConditionList;
	}
	
	public static final class Builder {
		private final List<CommandCondition> commandConditionList = new ArrayList<>();
		
		public Builder append(CommandCondition commandCondition){
			commandConditionList.add(commandCondition);
			return this;
		}
		public Builder append(Condition condition, MateCommand command){
			return append(new CommandCondition(condition, command));
		}
		public CommandSequence build(){
			return new CommandSequence(commandConditionList);
		}
	}
}
