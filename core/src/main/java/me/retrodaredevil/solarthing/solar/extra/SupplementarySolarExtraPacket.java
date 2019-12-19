package me.retrodaredevil.solarthing.solar.extra;

import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;

/**
 * {@link SupplementarySolarExtraPacket}s represent data that is usually accumulated or calculated from {@link me.retrodaredevil.solarthing.solar.SolarStatusPacket}s.
 * They are supplementary to those packets.
 */
public interface SupplementarySolarExtraPacket extends SolarExtraPacket, SupplementaryIdentifiable {
}
