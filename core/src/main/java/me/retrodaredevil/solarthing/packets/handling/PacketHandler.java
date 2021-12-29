package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

/**
 * Handles a {@link PacketCollection}, usually by saving it somewhere
 */
public interface PacketHandler {
	/**
	 * Handles the packetCollection
	 * @param packetCollection The {@link PacketCollection} to handle
	 */
	void handle(PacketCollection packetCollection) throws PacketHandleException;

	class Defaults {
		public static final PacketHandler HANDLE_NOTHING = (packetCollection) -> {};
	}
}
