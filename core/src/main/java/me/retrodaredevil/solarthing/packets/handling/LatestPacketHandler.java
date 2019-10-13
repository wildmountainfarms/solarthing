package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public class LatestPacketHandler implements PacketHandler {
	private final boolean instantOnly;
	private PacketCollection lastCollection = null;
	
	public LatestPacketHandler(boolean instantOnly) {
		this.instantOnly = instantOnly;
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) {
		if(!instantOnly || wasInstant){
			lastCollection = packetCollection;
		}
	}
	public PacketCollection getLatestPacketCollection(){
		return lastCollection;
	}
}
