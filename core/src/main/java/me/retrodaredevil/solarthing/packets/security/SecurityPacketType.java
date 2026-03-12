package me.retrodaredevil.solarthing.packets.security;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum SecurityPacketType implements DocumentedPacketType {
	INTEGRITY_PACKET,
	LARGE_INTEGRITY_PACKET,
	AUTH_NEW_SENDER
}
