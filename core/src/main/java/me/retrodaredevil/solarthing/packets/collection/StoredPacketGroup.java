package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;

/**
 * Represents a packet that is stored in the database under some {@link StoredIdentifier}
 */
public interface StoredPacketGroup extends BasicPacketGroup {
	@NotNull StoredIdentifier getStoredIdentifier();

}
