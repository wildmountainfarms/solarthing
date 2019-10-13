package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PrintPacketHandleExceptionWrapper implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PrintPacketHandleExceptionWrapper.class);
	
	private final PacketHandler packetHandler;
	
	
	public PrintPacketHandleExceptionWrapper(PacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) {
		try {
			packetHandler.handle(packetCollection, wasInstant);
		} catch (PacketHandleException e) {
			LOGGER.error("Caught PacketHandleException from " + packetHandler, e);
		}
	}
}
