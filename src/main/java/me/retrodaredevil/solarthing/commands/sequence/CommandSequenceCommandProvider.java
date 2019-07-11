package me.retrodaredevil.solarthing.commands.sequence;

import me.retrodaredevil.solarthing.commands.Command;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.ConditionCommand;
import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.commands.sequence.condition.ConditionTask;
import me.retrodaredevil.solarthing.commands.source.Source;

import java.util.LinkedList;
import java.util.Queue;

import static java.util.Objects.requireNonNull;

public class CommandSequenceCommandProvider<T extends Command> implements CommandProvider<T> {
	private final CommandSequenceProvider<T> commandSequenceProvider;
	
	private Source currentSource = null;
	private Queue<ConditionCommand<T>> commands = null;
	
	private ConditionCommand<T> currentCommand = null;
	private ConditionTask currentTask = null;
	
	public CommandSequenceCommandProvider(CommandSequenceProvider<T> commandSequenceProvider) {
		this.commandSequenceProvider = commandSequenceProvider;
	}
	
	@Override
	public SourcedCommand<T> pollCommand() {
		Source currentSource = this.currentSource;
		ConditionCommand<T> currentCommand = this.currentCommand;
		if(currentCommand == null){
			Queue<ConditionCommand<T>> commands = this.commands;
			
			if(currentSource == null || commands == null || commands.isEmpty()){
				SourcedCommandSequence<T> sourcedCommandSequence = commandSequenceProvider.pollCommandSequence();
				if(sourcedCommandSequence != null){
					currentSource = sourcedCommandSequence.getSource();
					commands = new LinkedList<>(sourcedCommandSequence.getCommandSequence().getConditionCommandList());
				}
			}
			if(currentSource != null && commands != null){
				this.currentSource = currentSource;
				this.commands = commands;
				currentCommand = commands.poll();
				this.currentCommand = currentCommand;
			}
		} else {
			requireNonNull(currentSource);
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
				return new SourcedCommand<>(currentSource, currentCommand.getCommand());
			}
		}
		return null;
	}
}
