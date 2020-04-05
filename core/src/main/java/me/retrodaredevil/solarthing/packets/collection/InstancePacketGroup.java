package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

/**
 * Represents a {@link FragmentedPacketGroup} where each packet has the same fragment id.
 * <p>
 * Usually, this also means that each packet has the same timestamp which means that {@link #getDateMillis(Packet)} should return null
 */
public interface InstancePacketGroup extends FragmentedPacketGroup {
	@Override
	String getSourceId();
	Integer getFragmentId();

	@Override
	default Integer getFragmentId(Packet packet) {
		return getFragmentId();
	}
}
