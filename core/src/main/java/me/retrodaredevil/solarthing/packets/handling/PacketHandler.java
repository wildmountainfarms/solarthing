package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

/**
 * Handles a {@link PacketCollection}, usually by saving it somewhere
 */
public interface PacketHandler {
	/**
	 * Handles the packetCollection
	 * @param packetCollection The {@link PacketCollection} to handle
	 * @param wasInstant true if {@code packetCollection} was created instantly/reliably/recently
	 */
	void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException;

	class Defaults {
		public static final PacketHandler HANDLE_NOTHING = (packetCollection, wasInstant) -> {};
	}
}
