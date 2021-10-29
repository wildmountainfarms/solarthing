package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.packets.Packet;

/**
 * Represents a packet group where the dateMillis is the same for each packet.
 */
public interface BasicPacketGroup extends PacketGroup {
	/**
	 * @deprecated Use {@link #getDateMillis()} instead. All implementations of {@link BasicPacketGroup} assumes there is no fragmenting of packets going on, so there's only one
	 * 			   possibility for the dateMillis value
	 */
	@DefaultFinal
	@Deprecated
	@Override
	default Long getDateMillis(Packet packet) {
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
