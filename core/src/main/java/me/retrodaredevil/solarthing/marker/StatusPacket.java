package me.retrodaredevil.solarthing.marker;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a packet that is stored in a packet collection inside the {@link me.retrodaredevil.solarthing.SolarThingConstants#STATUS_DATABASE}
 */
@NullMarked
public interface StatusPacket extends DocumentedPacket {
}
