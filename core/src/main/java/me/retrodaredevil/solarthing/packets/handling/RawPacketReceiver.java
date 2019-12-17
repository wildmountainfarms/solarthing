package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;

public interface RawPacketReceiver {
	/**
	 * This is called if a packet was unable to be parsed.
	 */
	void updateGarbledData();
	/**
	 *
	 * @param newPackets The collection of packets or an empty collection
	 */
	void update(Collection<? extends Packet> newPackets);

	void updateNoNewData();

}
