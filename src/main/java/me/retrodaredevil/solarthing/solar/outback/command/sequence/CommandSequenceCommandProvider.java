package me.retrodaredevil.solarthing.solar.outback.command.sequence;

import me.retrodaredevil.solarthing.solar.outback.command.CommandCondition;
import me.retrodaredevil.solarthing.solar.outback.command.CommandProvider;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.condition.ConditionTask;

import java.util.LinkedList;
import java.util.Queue;

public class CommandSequenceCommandProvider implements CommandProvider {
	private final CommandSequenceProvider commandSequenceProvider;
	
	private Queue<CommandCondition> commands = null;
	private CommandCondition currentCommand = null;
	private ConditionTask currentTask = null;
	
	public CommandSequenceCommandProvider(CommandSequenceProvider commandSequenceProvider) {
		this.commandSequenceProvider = commandSequenceProvider;
	}
	
	@Override
	public MateCommand pollCommand() {
		CommandCondition currentCommand = this.currentCommand;
		if(currentCommand == null){
			Queue<CommandCondition> commands = this.commands;
			if(commands == null || commands.isEmpty()){
				CommandSequence commandSequence = commandSequenceProvider.pollCommandSequence();
				if(commandSequence != null){
					commands = new LinkedList<>(commandSequence.getCommandConditionList());
				}
			}
			if(commands != null){
				this.commands = commands;
				currentCommand = commands.poll();
				this.currentCommand = currentCommand;
			}
		}
		if(currentCommand != null){
			ConditionTask currentTask = this.currentTask;
			if(currentTask == null){
				currentTask = currentCommand.getCondition().start();
				this.currentTask = currentTask;
			}
			if(currentTask.isDone()){
				this.currentCommand = null;
				this.currentTask = null;
				return currentCommand.getCommand();
			}
		}
		return null;
	}
}
