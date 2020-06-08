package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public class LatestPacketHandler implements PacketHandler {
	private final boolean instantOnly;
	private PacketCollection lastCollection = null;

	public LatestPacketHandler(boolean instantOnly) {
		this.instantOnly = instantOnly;
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) {
		if(!instantOnly || instantType.isInstant()){
			lastCollection = packetCollection;
		}
	}
	public PacketCollection getLatestPacketCollection(){
		return lastCollection;
	}
}
