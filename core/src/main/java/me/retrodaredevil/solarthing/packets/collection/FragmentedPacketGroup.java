package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Packet;

/**
 * Represents a {@link PacketGroup} where individual packets could have different fragment IDs
 */
public interface FragmentedPacketGroup extends PacketGroup {
	@NotNull String getSourceId(Packet packet);
	int getFragmentId(Packet packet);

	default boolean hasSourceId(String sourceId) {
		for (Packet packet : getPackets()) {
			if (sourceId.equals(getSourceId(packet))) {
				return true;
			}
		}
		return false;
	}
	default boolean hasFragmentId(int fragmentId) {
		for (Packet packet : getPackets()) {
			if (fragmentId == getFragmentId(packet)) {
				return true;
			}
		}
		return false;
	}
}
