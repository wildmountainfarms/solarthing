package me.retrodaredevil.solarthing.couchdb;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.JsonPacketReceiver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CouchDbPacketRetrieverHandler implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketRetrieverHandler.class);

	private final CouchDbPacketRetriever couchDbPacketRetriever;
	private final JsonPacketReceiver jsonPacketReceiver;

	private final ExecutorService executorService;

	public CouchDbPacketRetrieverHandler(CouchDbPacketRetriever couchDbPacketRetriever, JsonPacketReceiver jsonPacketReceiver, boolean separateThread){
		this.couchDbPacketRetriever = couchDbPacketRetriever;
		this.jsonPacketReceiver = jsonPacketReceiver;
		executorService = separateThread ? Executors.newSingleThreadExecutor() : null;
	}
	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
		if (executorService == null) {
			jsonPacketReceiver.receivePackets(couchDbPacketRetriever.query());
		} else {
			executorService.execute(() -> {
				try {
					jsonPacketReceiver.receivePackets(couchDbPacketRetriever.query());
				} catch (PacketHandleException e) {
					LOGGER.error("Got error while querying packets", e);
				}
			});
		}
	}
}
