package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.collection.PacketGroup;

public interface PacketGroupProvider {
	/**
	 * Note: Depending on the implementation, it may return null values. This class is made to be flexible, so non-null values are not guaranteed.
	 * @return The latest packet group.
	 */
	PacketGroup getPacketGroup();
}
