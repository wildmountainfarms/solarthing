package me.retrodaredevil.solarthing.packets;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;

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
	 * After this method is called, mutating {@code packets} will have no effect and is tolerated.
	 * @param packets The packets to save.
	 */
	@Deprecated
	default void savePackets(Collection<Packet> packets){
		savePacketCollection(PacketCollections.createFromPackets(packets));
	}
}
