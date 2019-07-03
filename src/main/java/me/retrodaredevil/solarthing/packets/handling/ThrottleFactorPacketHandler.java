package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public class ThrottleFactorPacketHandler implements PacketHandler {
	private final PacketHandler packetHandler;
	private final int throttleFactor;
	private final boolean instantOnly;
	
	private int counter = 0;
	
	public ThrottleFactorPacketHandler(PacketHandler packetHandler, int throttleFactor, boolean instantOnly) {
		this.packetHandler = packetHandler;
		this.throttleFactor = throttleFactor;
		this.instantOnly = instantOnly;
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		if(instantOnly && !wasInstant){
			return; // return and don't increment counter
		}
		if(counter++ % throttleFactor == 0){
			packetHandler.handle(packetCollection, wasInstant);
		}
	}
}
