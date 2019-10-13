package me.retrodaredevil.solarthing.outhouse;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

/**
 * Represents different packet types for "Outhouse" packets.
 * <p>
 * NOTE: More enum constants may be added
 */
public enum OuthousePacketType implements DocumentedPacketType {
	OCCUPANCY,
	WEATHER,
	DOOR
}
