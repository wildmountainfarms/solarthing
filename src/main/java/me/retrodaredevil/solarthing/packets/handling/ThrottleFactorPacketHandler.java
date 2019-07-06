package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public class ThrottleFactorPacketHandler implements PacketHandler {
	private final PacketHandler packetHandler;
	private final int throttleFactor;
	private final boolean instantOnly;
	private final PacketHandler otherPacketHandler;
	
	private int counter = 0;
	
	/**
	 * @param packetHandler The packet handler
	 * @param throttleFactor The throttle factor. {@code packetHandler} will be called every nth packet, where n is this value.
	 * @param instantOnly true if neither {@code packetHandler} nor {@code otherPacketHandler} should be called if {@code wasInstant} is false
	 * @param otherPacketHandler This handler is called when {@code packetHandler} is not called.
	 */
	public ThrottleFactorPacketHandler(PacketHandler packetHandler, int throttleFactor, boolean instantOnly, PacketHandler otherPacketHandler) {
		this.packetHandler = packetHandler;
		this.throttleFactor = throttleFactor;
		this.instantOnly = instantOnly;
		this.otherPacketHandler = otherPacketHandler;
	}
	public ThrottleFactorPacketHandler(PacketHandler packetHandler, int throttleFactor, boolean instantOnly){
		this(packetHandler, throttleFactor, instantOnly, PacketHandler.Defaults.HANDLE_NOTHING);
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		if(instantOnly && !wasInstant){
			return; // return and don't increment counter
		}
		if(counter++ % throttleFactor == 0){
			packetHandler.handle(packetCollection, wasInstant);
		} else {
			otherPacketHandler.handle(packetCollection, wasInstant);
		}
	}
}
