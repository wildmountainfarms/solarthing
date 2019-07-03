package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.solar.outback.MateCommand;

import java.io.IOException;
import java.io.OutputStream;

import static java.util.Objects.requireNonNull;

public class MateCommandSender implements OnDataReceive {
	private final CommandProvider commandProvider;
	private final OutputStream outputStream;
	
	public MateCommandSender(CommandProvider commandProvider, OutputStream outputStream) {
		this.commandProvider = requireNonNull(commandProvider);
		this.outputStream = requireNonNull(outputStream);
	}
	
	public void onDataReceive(boolean firstData, boolean wasInstant) {
		if(firstData && wasInstant){
			MateCommand command = commandProvider.pollCommand();
			if(command != null){
				try {
					command.send(outputStream);
					System.out.println("\nSent command: " + command + " at " + System.currentTimeMillis());
				} catch (IOException e) {
					throw new RuntimeException("Unable to send command: " + command, e);
				}
			}
		}
	}
	public interface CommandProvider {
		MateCommand pollCommand();
	}
}
