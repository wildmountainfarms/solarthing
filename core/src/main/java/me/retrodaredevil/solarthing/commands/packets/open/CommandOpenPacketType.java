package me.retrodaredevil.solarthing.commands.packets.open;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum CommandOpenPacketType implements DocumentedPacketType {
	REQUEST_COMMAND,
	SCHEDULE_COMMAND,
	REQUEST_FLAG,
	DELETE_ALTER,
	REQUEST_HEARTBEAT,
	FLAG_ALIAS_ADD,
	FLAG_ALIAS_DELETE,
}
