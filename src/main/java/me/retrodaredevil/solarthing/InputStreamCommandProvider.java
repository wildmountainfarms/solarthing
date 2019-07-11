package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.commands.Command;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.commands.source.Source;
import me.retrodaredevil.util.InputStreamSplitter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class InputStreamCommandProvider<T extends Command> implements CommandProvider<T> {
	private final InputStreamSplitter inputStreamSplitter;
	
	private final Source source;
	private final Map<String, T> commandMap;
	
	public InputStreamCommandProvider(InputStream inputStream, Source source, Map<String, T> commandMap) {
		this.inputStreamSplitter = new InputStreamSplitter(inputStream);
		this.source = requireNonNull(source);
		this.commandMap = requireNonNull(commandMap);
	}
	
	public static<T extends Command> InputStreamCommandProvider<T> createFrom(InputStream inputStream, Source source, Collection<T> commands){
		Map<String, T> commandMap = new HashMap<>();
		for(T command : commands){
			commandMap.put(command.getCommandName(), command);
		}
		return new InputStreamCommandProvider<>(inputStream, source, commandMap);
	}
	
	@Override
	public SourcedCommand<T> pollCommand() {
		final Queue<String> lines;
		try {
			lines = inputStreamSplitter.getQueue();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		T command = null;
		while(command == null && !lines.isEmpty()) {
			command = commandMap.get(lines.poll());
		}
		if(command == null){
			return null;
		}
		return new SourcedCommand<>(source, command);
	}
}
