package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public class LatestPacketHandler implements PacketHandler {
	private volatile PacketCollection lastCollection = null;

	public LatestPacketHandler() {
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) {
		lastCollection = packetCollection;
	}
	public PacketCollection getLatestPacketCollection(){
		return lastCollection;
	}
}
