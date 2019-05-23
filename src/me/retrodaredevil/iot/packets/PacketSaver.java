package me.retrodaredevil.iot.packets;

import java.util.Collection;

public interface PacketSaver {
	/**
	 * Saves the packetCollection
	 * @param packetCollection
	 */
	void savePacketCollection(PacketCollection packetCollection);

	/**
	 * Should create a new {@link PacketCollection} and do the same thing as {@link #savePacketCollection(PacketCollection)}
	 * <p>
	 * After this method is called, mutating packets will have no effect and is tolerated.
	 * @param packets The packets to save.
	 */
	void savePackets(Collection<Packet> packets);
}
