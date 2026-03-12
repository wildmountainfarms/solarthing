package me.retrodaredevil.solarthing.solar.event;

import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;
import org.jspecify.annotations.NullMarked;

/**
 * Represents {@link SolarEventPacket}s that also inherit {@link SupplementaryIdentifiable}
 */
@NullMarked
public interface SupplementarySolarEventPacket extends SolarEventPacket, SupplementaryIdentifiable {
}
