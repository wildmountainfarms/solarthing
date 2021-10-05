package me.retrodaredevil.solarthing.commands.packets.open;

import me.retrodaredevil.solarthing.annotations.WorkInProgress;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

public enum CommandOpenPacketType implements DocumentedPacketType {
	REQUEST_COMMAND,
	@WorkInProgress
	SCHEDULE_COMMAND,
	@WorkInProgress
	REQUEST_FLAG,
}
