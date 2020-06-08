package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class used to retry handling a packet at a later time if it fails
 */
public class RetryFailedPacketHandler implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(RetryFailedPacketHandler.class);

	private final PacketHandler packetHandler;
	private final int packetCollectionsToKeepOnFail;
	private final List<PacketCollection> packetCollectionList = new ArrayList<>();

	public RetryFailedPacketHandler(PacketHandler packetHandler, int packetCollectionsToKeepOnFail) {
		this.packetHandler = packetHandler;
		this.packetCollectionsToKeepOnFail = packetCollectionsToKeepOnFail;
		if(packetCollectionsToKeepOnFail <= 0){
			throw new IllegalArgumentException("packetCollectionsToKeepOnFail must be > 0! was: " + packetCollectionsToKeepOnFail);
		}
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) {
		packetCollectionList.add(packetCollection);
		for (Iterator<PacketCollection> iterator = packetCollectionList.iterator(); iterator.hasNext(); ) {
			PacketCollection element = iterator.next();
			try {
				packetHandler.handle(element, instantType);
				iterator.remove();
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't packet collection id: " + packetCollection.getDbId() + " dateMillis: " + packetCollection.getDateMillis(), ". Might try again later.", e);
			}
		}
		while(packetCollectionList.size() > packetCollectionsToKeepOnFail){
			PacketCollection element = packetCollectionList.remove(0); // remove the first few
			LOGGER.warn("Because packetCollectionsToKeepOnFail=" + packetCollectionsToKeepOnFail + ", we have to remove id: " + element.getDbId() + " dateMillis: " + element.getDateMillis() + ". We will never try to handle is again. It contained " + element.getPackets().size() + " packets");
		}
	}
}
