package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;

public class PrintPacketHandleExceptionWrapper implements PacketHandler {
	private static final Logger LOGGER = LogManager.getLogger(PrintPacketHandleExceptionWrapper.class);
	
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
