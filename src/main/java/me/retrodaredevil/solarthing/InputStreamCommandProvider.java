package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.solar.outback.command.CommandProvider;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.util.InputStreamSplitter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class InputStreamCommandProvider implements CommandProvider {
	private final InputStreamSplitter inputStreamSplitter;
	private final Map<String, MateCommand> commandMap;
	
	public InputStreamCommandProvider(InputStream inputStream, Map<String, MateCommand> commandMap) {
		this.inputStreamSplitter = new InputStreamSplitter(inputStream);
		this.commandMap = commandMap;
	}
	
	public static InputStreamCommandProvider createFrom(InputStream inputStream, Collection<MateCommand> commands){
		Map<String, MateCommand> commandMap = new HashMap<>();
		for(MateCommand command : commands){
			commandMap.put(command.getCommandName(), command);
		}
		return new InputStreamCommandProvider(inputStream, commandMap);
	}
	
	@Override
	public MateCommand pollCommand() {
		final Queue<String> lines;
		try {
			lines = inputStreamSplitter.getQueue();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		MateCommand command = null;
		while(command == null && !lines.isEmpty()) {
			command = commandMap.get(lines.poll());
		}
		return command;
	}
}
