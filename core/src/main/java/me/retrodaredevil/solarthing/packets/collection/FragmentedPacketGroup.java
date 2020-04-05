package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

/**
 * Represents a {@link PacketGroup} where individual packets could have different fragment IDs
 */
public interface FragmentedPacketGroup extends PacketGroup {
	String getSourceId();
	Integer getFragmentId(Packet packet);
}
