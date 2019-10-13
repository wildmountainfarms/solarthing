package me.retrodaredevil.solarthing.solar.outback.command.packets;

import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

public interface SuccessMateCommandPacket extends MateCommandFeedbackPacket {
	/**
	 * Should be serialized as "command" (as a String)
	 * @return The {@link MateCommand} that was successfully sent to the MATE
	 */
	MateCommand getCommand();
	
	/**
	 * Should be serialized as "source"
	 *
	 * @return The string representing the source of the command
	 */
	String getSource();
}
