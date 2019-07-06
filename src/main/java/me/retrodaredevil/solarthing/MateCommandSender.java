package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.solar.outback.command.CommandProvider;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class MateCommandSender implements OnDataReceive {
	private final CommandProvider commandProvider;
	private final OutputStream outputStream;
	private final Collection<MateCommand> allowedCommands;
	
	public MateCommandSender(CommandProvider commandProvider, OutputStream outputStream, Collection<MateCommand> allowedCommands) {
		this.commandProvider = requireNonNull(commandProvider);
		this.outputStream = requireNonNull(outputStream);
		this.allowedCommands = allowedCommands;
	}
	
	public void onDataReceive(boolean firstData, boolean wasInstant) {
		if(firstData && wasInstant){
			MateCommand command = commandProvider.pollCommand();
			if(command != null){
				if(!allowedCommands.contains(command)){
					System.err.println("Command: " + command + " is not allowed!");
					return;
				}
				try {
					command.send(outputStream);
					System.out.println("\nSent command: " + command + " at " + System.currentTimeMillis());
				} catch (IOException e) {
					throw new RuntimeException("Unable to send command: " + command, e);
				}
			}
		}
	}
}
