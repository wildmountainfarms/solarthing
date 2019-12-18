package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;

public interface RawPacketReceiver {
	/**
	 * Called when data was received, but that data was not parsable (it was garbled)
	 */
	void updateGarbledData();
	/**
	 * This is called if data was received. {@code newPackets} may be an empty collection because although data was received,
	 * that data may represent an entire packet
	 * @param newPackets The collection of packets or an empty collection
	 */
	void update(Collection<? extends Packet> newPackets);

	/**
	 * Called when no new data is received
	 */
	void updateNoNewData();

}
