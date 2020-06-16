package me.retrodaredevil.solarthing.packets.security;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

public enum SecurityPacketType implements DocumentedPacketType {
	INTEGRITY_PACKET,
	LARGE_INTEGRITY_PACKET,
	AUTH_NEW_SENDER
}
