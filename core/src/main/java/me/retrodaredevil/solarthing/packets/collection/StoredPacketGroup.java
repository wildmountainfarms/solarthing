package me.retrodaredevil.solarthing.packets.collection;

import org.jspecify.annotations.NonNull;

/**
 * Represents a packet that is stored in the database under some {@link StoredIdentifier}
 */
public interface StoredPacketGroup extends BasicPacketGroup {
	@NonNull StoredIdentifier getStoredIdentifier();

}
