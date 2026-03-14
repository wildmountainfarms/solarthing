package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface PacketGroupProvider {
	/**
	 * Note: Depending on the implementation, it may return null values. This class is made to be flexible, so non-null values are not guaranteed.
	 * @return The latest packet group.
	 */
	@Nullable PacketGroup getPacketGroup();
}
