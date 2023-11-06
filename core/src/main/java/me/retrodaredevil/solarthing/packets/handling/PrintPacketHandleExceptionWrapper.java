package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.SolarThingConstants;
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
	public void handle(PacketCollection packetCollection) {
		try {
			packetHandler.handle(packetCollection);
		} catch (CommonPacketHandleException e) {
			LOGGER.debug("Caught a common packet handle exception with message: " + e.getMessage());
			LOGGER.debug(SolarThingConstants.NO_CONSOLE, "Stacktrace of above exception", e);
		} catch (PacketHandleException e) {
			LOGGER.error("Caught PacketHandleException from " + packetHandler + ". Message: " + e.getMessage() + ". (More info in log file)");
			LOGGER.debug(SolarThingConstants.NO_CONSOLE, "Caught PacketHandleException from " + packetHandler, e);
		}
	}
}
