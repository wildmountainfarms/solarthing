package me.retrodaredevil.solarthing.packets;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public interface PacketSaver {
	/**
	 * Saves the packetCollection
	 * @param packetCollection The {@link PacketCollection} to save
	 */
	void savePacketCollection(PacketCollection packetCollection) throws PacketSaveException;

}
