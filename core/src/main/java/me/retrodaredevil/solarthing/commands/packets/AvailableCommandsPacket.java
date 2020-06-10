package me.retrodaredevil.solarthing.commands.packets;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.commands.CommandInfo;

import java.util.List;

public interface AvailableCommandsPacket extends CommandStatusPacket {
	@Override
	default @NotNull CommandStatusPacketType getPacketType() {
		return CommandStatusPacketType.AVAILABLE_COMMANDS;
	}

	List<CommandInfo> getCommandInfoList();

}
