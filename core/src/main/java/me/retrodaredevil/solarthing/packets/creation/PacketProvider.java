package me.retrodaredevil.solarthing.packets.creation;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;

public interface PacketProvider {
	Collection<? extends Packet> createPackets();
}
