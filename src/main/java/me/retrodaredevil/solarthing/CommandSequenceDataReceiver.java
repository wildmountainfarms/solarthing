package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.commands.Command;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.sequence.CommandSequence;
import me.retrodaredevil.solarthing.commands.sequence.CommandSequenceCommandProvider;
import me.retrodaredevil.solarthing.commands.sequence.SourcedCommandSequence;
import me.retrodaredevil.solarthing.commands.source.Sources;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CommandSequenceDataReceiver<T extends Command> implements DataReceiver {
	private final Queue<SourcedCommandSequence<T>> queue = new LinkedList<>();
	private final CommandProvider commandProvider = new CommandSequenceCommandProvider<>(queue::poll);
	
	private final Map<String, CommandSequence<T>> commandSequenceMap;
	
	public CommandSequenceDataReceiver(Map<String, CommandSequence<T>> commandSequenceMap) {
		this.commandSequenceMap = commandSequenceMap;
	}
	
	@Override
	public void receiveData(String sender, long dateMillis, String data) {
		CommandSequence<T> requested = commandSequenceMap.get(data);
		if(requested != null){
			queue.add(new SourcedCommandSequence<>(Sources.createNamed("from:" + sender + ",at:" + dateMillis + ",data:" + data), requested));
			System.out.println(sender + " has requested command sequence: " + data);
		}
	}
	
	public CommandProvider getCommandProvider(){
		return commandProvider;
	}
	
}
