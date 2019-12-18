package me.retrodaredevil.solarthing.packets.creation;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;
import java.util.List;

@FunctionalInterface
public interface PacketListUpdater {
	void updatePackets(List<Packet> packets);
}
