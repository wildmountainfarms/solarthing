package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.commands.command.CommandProvider;
import me.retrodaredevil.solarthing.commands.command.OnCommandExecute;
import me.retrodaredevil.solarthing.commands.command.SourcedCommand;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class MateCommandSender implements OnDataReceive {
	private static final Logger LOGGER = LoggerFactory.getLogger(MateCommandSender.class);
	private final CommandProvider<MateCommand> commandProvider;
	private final OutputStream outputStream;
	private final Collection<MateCommand> allowedCommands;
	private final OnCommandExecute<MateCommand> onCommandExecute;

	public MateCommandSender(CommandProvider<MateCommand> commandProvider, OutputStream outputStream, Collection<MateCommand> allowedCommands, OnCommandExecute<MateCommand> onCommandExecute) {
		this.commandProvider = requireNonNull(commandProvider);
		this.outputStream = requireNonNull(outputStream);
		this.allowedCommands = requireNonNull(allowedCommands);
		this.onCommandExecute = requireNonNull(onCommandExecute);
	}

	public void onDataReceive(boolean firstData, InstantType instantType) {
		if(firstData && instantType.isInstant()){
			SourcedCommand<MateCommand> sourcedCommand = commandProvider.pollCommand();
			if(sourcedCommand != null){
				MateCommand command = sourcedCommand.getCommand();
				if(!allowedCommands.contains(command)){
					LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Command: " + command + " is not allowed!");
					return;
				}
				/*
				I can confirm that if you don't send the command in the correct time window, the Mate will not receive it

				This solution is only here because instantType is not always reliable.
				 */
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Sending command: " + command);
				try {
					Thread.sleep(10);
					for (int i = 0; i <= 20; i++) { // send this over the period of 0.8 second so we can be sure it got received
						if (i != 0) {
							Thread.sleep(100);
						}
						command.send(outputStream);
					}
				} catch (IOException | InterruptedException e) {
					LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Unable to send command: " + command, e);
					return;
				}
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Sent command: " + command + " at " + System.currentTimeMillis());
				onCommandExecute.onCommandExecute(sourcedCommand);
			}
		}
	}
}
