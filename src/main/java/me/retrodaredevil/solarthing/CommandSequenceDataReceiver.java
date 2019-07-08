package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.solar.outback.command.CommandProvider;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.CommandSequence;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.CommandSequenceCommandProvider;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CommandSequenceDataReceiver implements DataReceiver {
	private final Queue<CommandSequence> queue = new LinkedList<>();
	private final CommandProvider commandProvider = new CommandSequenceCommandProvider(queue::poll);
	
	private final Map<String, CommandSequence> commandSequenceMap;
	
	public CommandSequenceDataReceiver(Map<String, CommandSequence> commandSequenceMap) {
		this.commandSequenceMap = commandSequenceMap;
	}
//	public static CouchDbPacketRetriever createCouchDbCommandRetriever(CouchProperties properties, PublicKeyLookUp publicKeyLookUp){
//		return new CouchDbPacketRetriever(properties, "commands", new SecurityPacketReceiver(publicKeyLookUp, dataReceiver));
//	}
	
	@Override
	public void receiveData(String sender, long dateMillis, String data) {
		CommandSequence requested = commandSequenceMap.get(data);
		if(requested != null){
			queue.add(requested);
			System.out.println(sender + " has requested command sequence: " + data);
		}
	}
	
	public CommandProvider getCommandProvider(){
		return commandProvider;
	}
	
}
