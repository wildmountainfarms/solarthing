package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public class ThrottleFactorPacketHandler implements PacketHandler {
	private final PacketHandler packetHandler;
	private final int throttleFactor;
	
	private int counter = 0;
	
	public ThrottleFactorPacketHandler(PacketHandler packetHandler, int throttleFactor) {
		this.packetHandler = packetHandler;
		this.throttleFactor = throttleFactor;
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		if(counter++ % throttleFactor == 0){
			packetHandler.handle(packetCollection, wasInstant);
		}
	}
}
