package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Packet;

/**
 * Represents a packet that is stored in the database under some {@link StoredIdentifier}
 */
public interface StoredPacketGroup extends PacketGroup {
	@NotNull StoredIdentifier getStoredIdentifier();

	@Deprecated
	@Override
	default Long getDateMillis(Packet packet) {
		return null;
	}

	@Deprecated
	@Override
	default long getDateMillisOrKnown(Packet packet) {
		return getDateMillis();
	}
}
