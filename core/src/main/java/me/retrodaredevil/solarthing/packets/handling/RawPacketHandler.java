package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;

public interface RawPacketHandler {
	/**
	 *
	 * @param packets The collection of packets or an empty collection
	 * @param instant true if instant, false otherwise
	 */
	void add(Collection<? extends Packet> packets, boolean instant);
}
