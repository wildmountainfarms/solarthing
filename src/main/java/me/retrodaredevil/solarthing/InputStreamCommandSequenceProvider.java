package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.solar.outback.command.sequence.CommandSequence;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.CommandSequenceProvider;
import me.retrodaredevil.util.InputStreamSplitter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Queue;

public class InputStreamCommandSequenceProvider implements CommandSequenceProvider {
	private final InputStreamSplitter inputStreamSplitter;
	private final Map<String, CommandSequence> commandSequenceMap;
	
	public InputStreamCommandSequenceProvider(InputStream inputStream, Map<String, CommandSequence> commandSequenceMap) {
		this.inputStreamSplitter = new InputStreamSplitter(inputStream);
		this.commandSequenceMap = commandSequenceMap;
	}
	
	@Override
	public CommandSequence pollCommandSequence() {
		final Queue<String> lines;
		try {
			lines = inputStreamSplitter.getQueue();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		CommandSequence commandSequence = null;
		while(commandSequence == null && !lines.isEmpty()) {
			commandSequence = commandSequenceMap.get(lines.poll());
		}
		return commandSequence;
	}
}
