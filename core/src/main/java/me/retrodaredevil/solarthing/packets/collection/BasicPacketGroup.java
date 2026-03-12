package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a packet group where the dateMillis is the same for each packet.
 */
@NullMarked
public interface BasicPacketGroup extends PacketGroup {
	/**
	 * @deprecated Use {@link #getDateMillis()} instead. All implementations of {@link BasicPacketGroup} assumes there is no fragmenting of packets going on, so there's only one
	 * 			   possibility for the dateMillis value
	 */
	@DefaultFinal
	@Deprecated
	@Override
	default @Nullable Long getDateMillis(Packet packet) {
		return null;
	}

	/**
	 * @deprecated Use {@link #getDateMillis()} instead. All implementations of {@link BasicPacketGroup} assumes there is no fragmenting of packets going on, so there's only one
	 * 			   possibility for the dateMillis value
	 */
	@DefaultFinal
	@Deprecated
	@Override
	default long getDateMillisOrKnown(Packet packet) {
		return getDateMillis();
	}
}
