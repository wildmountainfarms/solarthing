package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.creation.PacketListUpdater;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.handling.RawPacketReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class TimedPacketReceiver implements RawPacketReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimedPacketReceiver.class);

	private final PacketHandler packetHandler;
	private final PacketCollectionIdGenerator idGenerator;
	private final long samePacketTime;
	private final OnDataReceive onDataReceive;
	private final PacketListUpdater packetListUpdater;

	private long lastFirstReceivedData = Long.MIN_VALUE; // the last time a packet was added to packetList

	/** A list that piles up packets and handles when needed. May be cleared */
	private final List<Packet> packetList = new ArrayList<>(); //
	private boolean instant = false;

	/**
	 *
	 * @param packetHandler The packet handler that handles a {@link me.retrodaredevil.solarthing.packets.collection.PacketCollection} {@code samePacketTime} millis after the last packet has been created
	 * @param idGenerator The {@link PacketCollectionIdGenerator} used to get the id to save packets with
	 * @param samePacketTime The maximum amount of time allowed between packets that will be grouped together in a {@link me.retrodaredevil.solarthing.packets.collection.PacketCollection}
	 * @param onDataReceive This is called whenever data is received from {@code in}
	 * @param packetListUpdater A {@link PacketListUpdater} which adds additional packets before saving
	 */
	public TimedPacketReceiver(PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, long samePacketTime, OnDataReceive onDataReceive, PacketListUpdater packetListUpdater){
		this.packetHandler = requireNonNull(packetHandler);
		this.idGenerator = requireNonNull(idGenerator);
		this.samePacketTime = samePacketTime;
		this.onDataReceive = requireNonNull(onDataReceive);
		this.packetListUpdater = requireNonNull(packetListUpdater);
	}

	@Override
	public void updateGarbledData() {
		instant = false;
		packetList.clear();
	}

	@Override
	public void update(Collection<? extends Packet> newPackets) {
		long now = System.currentTimeMillis();
		boolean firstData = lastFirstReceivedData + samePacketTime < now;
		if (firstData) {
			lastFirstReceivedData = now; // set this to the first time we get bytes
		}
		onDataReceive.onDataReceive(firstData, instant);
		packetList.addAll(newPackets);
	}

	@Override
	public void updateNoNewData() {
		long now = System.currentTimeMillis();
		if (lastFirstReceivedData + samePacketTime < now) { // if there's no new packets coming any time soon
			if (packetList.isEmpty()) {
				instant = true;
			} else {
				final boolean wasInstant = instant;
				instant = false;
				try {
					LOGGER.debug("handling above packet(s). packetList.size(): " + packetList.size() + " instant: " + wasInstant);
					packetListUpdater.updatePackets(packetList);
					LOGGER.debug("We may have added some packets. packetList.size(): " + packetList.size());
					packetHandler.handle(PacketCollections.createFromPackets(packetList, idGenerator), wasInstant);
				} catch(PacketHandleException ex){
					LOGGER.error("Was unable to handle " + packetList.size() + " packet(s).", ex);
				} finally {
					packetList.clear();
				}
			}
		}
	}
}
