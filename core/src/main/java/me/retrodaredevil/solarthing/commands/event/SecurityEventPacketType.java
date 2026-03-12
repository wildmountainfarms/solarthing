package me.retrodaredevil.solarthing.commands.event;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum SecurityEventPacketType implements DocumentedPacketType {
	ACCEPT,
	REJECT,
}
