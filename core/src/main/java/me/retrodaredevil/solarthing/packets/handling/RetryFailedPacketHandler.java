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
 * <p>
 * Note that this will never throw a {@link PacketHandleException}. It will log the error, though
 * @deprecated Use {@link AsyncRetryingPacketHandler} instead.
 */
@Deprecated
public class RetryFailedPacketHandler implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(RetryFailedPacketHandler.class);
	/*
	TODO consider moving away from this class and using something involving a separate thread. It would be better if the retrying of uploading
	a PacketCollection didn't rely on a single PacketHandler being called twice. Ex: if a RetryFailedPacketHandler is called once and
	the given packetHandler fails to upload for a particular PacketCollection, then it should try again at some later time, no matter
	if RetryFailedPacketHandler is called again.
	 */
	/*
	TODO consider making sure that old, old packets don't get uploaded.
	For instance, if we are without internet for an hour, we may not want to upload old event packets when we come back online because
	then some clients of SolarThing may have bad caches for data. Yeah, caches aren't that great of an excuse not to upload data we have,
	but you have to draw the line somewhere. It could be packets that are over 5 minutes old, or it could be packets over 2 hours old we don't upload.
	We'll cross that bridge when we come there. The above comment says we should stop using this class anyway. It's almost ready to be deprecated
	 */

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
				LOGGER.error("Couldn't packet collection id: " + packetCollection.getDbId() + " dateMillis: " + packetCollection.getDateMillis(), e);
			}
		}
		while(packetCollectionList.size() > packetCollectionsToKeepOnFail){
			PacketCollection element = packetCollectionList.remove(0); // remove the first few
			LOGGER.warn("Because packetCollectionsToKeepOnFail=" + packetCollectionsToKeepOnFail + ", we have to remove id: " + element.getDbId() + " dateMillis: " + element.getDateMillis() + ". We will never try to handle is again. It contained " + element.getPackets().size() + " packets");
		}
	}
}
