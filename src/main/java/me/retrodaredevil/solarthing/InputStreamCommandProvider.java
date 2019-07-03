package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.solar.outback.MateCommand;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class InputStreamCommandProvider implements MateCommandSender.CommandProvider {
	private final InputStream inputStream;
	private final Map<String, MateCommand> commandMap;
	
	private final byte[] buffer = new byte[1024];
	private final Queue<String> lines = new LinkedList<>();
	private String currentLine = "";
	
	public InputStreamCommandProvider(InputStream inputStream, Map<String, MateCommand> commandMap) {
		this.inputStream = inputStream;
		this.commandMap = commandMap;
	}
	
	public static InputStreamCommandProvider createFromList(InputStream inputStream, List<MateCommand> commands){
		Map<String, MateCommand> commandMap = new HashMap<>();
		for(MateCommand command : commands){
			commandMap.put(command.getCommandName(), command);
		}
		return new InputStreamCommandProvider(inputStream, commandMap);
	}
	
	@Override
	public MateCommand pollCommand() {
		try {
			if(inputStream.available() > 0){
				int len = inputStream.read(buffer);
				String s = new String(buffer, 0, len);
				final StringBuilder currentLine = new StringBuilder(this.currentLine);
				for(char c : s.toCharArray()){
					if(c == '\n'){
						lines.add(currentLine.toString());
						currentLine.setLength(0);
					} else {
						currentLine.append(c);
					}
				}
				this.currentLine = currentLine.toString();
			}
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
