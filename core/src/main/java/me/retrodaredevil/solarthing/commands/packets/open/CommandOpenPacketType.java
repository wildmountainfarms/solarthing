package me.retrodaredevil.solarthing.commands.packets.open;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

public enum CommandOpenPacketType implements DocumentedPacketType {
	REQUEST_COMMAND,
	SCHEDULE_COMMAND,
	REQUEST_FLAG,
	DELETE_ALTER,
	REQUEST_HEARTBEAT,
}
