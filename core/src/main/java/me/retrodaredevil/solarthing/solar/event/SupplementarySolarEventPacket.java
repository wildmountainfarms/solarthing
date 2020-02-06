package me.retrodaredevil.solarthing.solar.event;

import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;

/**
 * Represents {@link SolarEventPacket}s that also inherit {@link SupplementaryIdentifiable}
 */
public interface SupplementarySolarEventPacket extends SolarEventPacket, SupplementaryIdentifiable {
}
