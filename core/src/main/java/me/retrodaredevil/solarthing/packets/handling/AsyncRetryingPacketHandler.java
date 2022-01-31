package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.util.PacketGroupNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AsyncRetryingPacketHandler implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncRetryingPacketHandler.class);

	private final PacketHandler packetHandler;

	private final ExecutorService mainExecutorService = Executors.newSingleThreadExecutor();
	private final ScheduledExecutorService reuploadExecutorService = Executors.newSingleThreadScheduledExecutor();

	private final NavigableSet<PacketGroupNode<PacketCollection>> packetCollections = new TreeSet<>();

	/**
	 * @param packetHandler The {@link PacketHandler} to use. Note: This should be thread safe, as {@link PacketHandler#handle(PacketCollection)} may be called in parallel
	 */
	public AsyncRetryingPacketHandler(PacketHandler packetHandler) {
		this.packetHandler = packetHandler;

		reuploadExecutorService.scheduleWithFixedDelay(
				() -> {
					// We don't have to worry about synchronizing at all because our executorService executes on a single thread
					final List<PacketGroupNode<PacketCollection>> toReupload;
					long removeBefore = System.currentTimeMillis() - 10 * 60 * 1000; // remove if before 10 minutes ago
					synchronized (packetCollections) {
						NavigableSet<PacketGroupNode<PacketCollection>> toRemove = packetCollections.headSet(new PacketGroupNode<>(removeBefore), true);
						if (!toRemove.isEmpty()) {
							LOGGER.info("Discarding " + toRemove.size() + " old packets. Will not try to reupload those using " + packetHandler);
							toRemove.clear();
						}

						if (!packetCollections.isEmpty()) {
							toReupload = new ArrayList<>(packetCollections);
						} else {
							toReupload = Collections.emptyList();
						}
					}
					if (!toReupload.isEmpty()) {
						LOGGER.debug("Going to try to reupload " + toReupload.size() + " packets using " + packetHandler);
						List<PacketGroupNode<PacketCollection>> successfullyUploaded = new ArrayList<>();
						try {
							for (PacketGroupNode<PacketCollection> node : toReupload) {
								PacketCollection packetCollection = node.getPacketGroup();
								packetHandler.handle(packetCollection);
								successfullyUploaded.add(node);
							}
							LOGGER.debug("Successfully reuploaded all " + toReupload.size() + " packets.");
						} catch (PacketHandleException ex) {
							LOGGER.error("Reuploaded " + successfullyUploaded.size() + " / " + toReupload.size() + " packets before getting exception", ex);
						}
						synchronized (packetCollections) {
							successfullyUploaded.forEach(packetCollections::remove);
						}
					}
				},
				10,
				10,
				TimeUnit.SECONDS
		);
	}

	@Override
	public void handle(PacketCollection packetCollection) {
		mainExecutorService.execute(() -> {
			try {
				packetHandler.handle(packetCollection);
			} catch (PacketHandleException e) {
				LOGGER.error("Error while uploading packet collection. ID: " + packetCollection.getDbId() + " dateMillis: " + packetCollection.getDateMillis() + ". Will retry using " + packetHandler);
				synchronized (packetCollection) {
					packetCollections.add(new PacketGroupNode<>(packetCollection));
				}
			}
		});
	}
}
