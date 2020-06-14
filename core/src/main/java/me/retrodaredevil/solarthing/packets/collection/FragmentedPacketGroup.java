package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Objects;

/**
 * Represents a {@link PacketGroup} where individual packets could have different fragment IDs
 */
public interface FragmentedPacketGroup extends SourcedPacketGroup {
	Integer getFragmentId(Packet packet);

	default boolean hasFragmentId(Integer fragmentId) {
		for (Packet packet : getPackets()) {
			if (Objects.equals(fragmentId, getFragmentId(packet))) {
				return true;
			}
		}
		return false;
	}
}
