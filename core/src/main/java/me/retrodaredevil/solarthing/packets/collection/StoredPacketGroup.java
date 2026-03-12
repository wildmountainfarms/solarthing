package me.retrodaredevil.solarthing.packets.collection;

import org.jspecify.annotations.NullMarked;

/**
 * Represents a packet that is stored in the database under some {@link StoredIdentifier}
 */
@NullMarked
public interface StoredPacketGroup extends BasicPacketGroup {
	StoredIdentifier getStoredIdentifier();

}
