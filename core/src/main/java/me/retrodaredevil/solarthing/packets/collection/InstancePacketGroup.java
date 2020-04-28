package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Objects;

/**
 * Represents a {@link FragmentedPacketGroup} where each packet has the same fragment id.
 * <p>
 * Usually, this also means that each packet has the same timestamp which means that {@link #getDateMillis(Packet)} should return null
 */
public interface InstancePacketGroup extends FragmentedPacketGroup {
	@Override
	String getSourceId();

	/**
	 * @return The fragmentId, which is the same for each packet
	 */
	Integer getFragmentId();

	/**
	 * @deprecated Use {@link #getFragmentId()} instead
	 * @param packet The packet to get the fragmentId of
	 * @return {@link #getFragmentId()}
	 */
	@Deprecated
	@Override
	default Integer getFragmentId(Packet packet) {
		return getFragmentId();
	}

	@Override
	default boolean hasFragmentId(Integer fragmentId) {
		return Objects.equals(fragmentId, getFragmentId());
	}
}
