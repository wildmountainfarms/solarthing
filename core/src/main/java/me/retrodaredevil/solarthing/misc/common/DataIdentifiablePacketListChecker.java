package me.retrodaredevil.solarthing.misc.common;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Crashes SolarThing if a user has incorrectly configured SolarThing by using the same data ID twice.
 */
public class DataIdentifiablePacketListChecker implements PacketListReceiver {
	@Override
	public void receive(List<Packet> packets) {
		Set<Integer> takenIds = new HashSet<>();
		for (Packet packet : packets) {
			if (packet instanceof DataIdentifiable) {
				DataIdentifiable dataIdentifiable = (DataIdentifiable) packet;
				int id = dataIdentifiable.getDataId();
				if (takenIds.contains(id)) {
					throw new IllegalStateException("Duplicate data ID! " + id); // maybe subclass IllegalStateException for a more meaningful name later
				}
				takenIds.add(id);
			}
		}
	}
}
