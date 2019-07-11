package me.retrodaredevil.solarthing.solar.outback.command.packets;

import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

public interface SuccessCommandPacket extends CommandFeedbackPacket {
	/**
	 * Should be serialized as "command" (as a String)
	 * @return The {@link MateCommand} that was successfully sent to the MATE
	 */
	MateCommand getCommand();
}
