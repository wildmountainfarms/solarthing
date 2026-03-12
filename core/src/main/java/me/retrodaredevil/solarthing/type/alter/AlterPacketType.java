package me.retrodaredevil.solarthing.type.alter;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum AlterPacketType implements DocumentedPacketType {
	FLAG,
	SCHEDULED_COMMAND,
	FLAG_ALIAS,
}
