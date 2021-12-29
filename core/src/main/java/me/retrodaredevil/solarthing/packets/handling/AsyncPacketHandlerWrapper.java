package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncPacketHandlerWrapper implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncPacketHandlerWrapper.class);

	private final PacketHandler packetHandler;

	private final ExecutorService executorService;

	public AsyncPacketHandlerWrapper(PacketHandler packetHandler) {
		this.packetHandler = packetHandler;

		executorService = Executors.newSingleThreadExecutor();
	}

	@Override
	public void handle(PacketCollection packetCollection) throws PacketHandleException {
		executorService.execute(() -> {
			try {
				packetHandler.handle(packetCollection);
			} catch (PacketHandleException e) {
				LOGGER.error("Got PacketHandleException while executing in separate thread.", e);
			}
		});
	}
}
