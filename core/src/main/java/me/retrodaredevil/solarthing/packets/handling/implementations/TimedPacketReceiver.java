package me.retrodaredevil.solarthing.packets.handling.implementations;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.RawPacketReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class TimedPacketReceiver implements RawPacketReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimedPacketReceiver.class);

	private final Duration samePacketTimeDuration;
	private final PacketListReceiver packetListReceiver;
	private final OnDataReceive onDataReceive;

	private Long lastFirstReceivedDataNanos = null; // the last time a packet was added to packetList

	/** A list that piles up packets and handles when needed. May be cleared */
	private final List<Packet> packetList = new ArrayList<>(); //
	private boolean instant = false;

	/**
	 * @param samePacketTimeDuration The maximum amount of time allowed between packets that will be grouped together in a {@link me.retrodaredevil.solarthing.packets.collection.PacketCollection}
	 * @param packetListReceiver A {@link PacketListReceiver} which adds additional packets before saving
	 * @param onDataReceive This is called whenever data is received from {@code in}
	 */
	public TimedPacketReceiver(Duration samePacketTimeDuration, PacketListReceiver packetListReceiver, OnDataReceive onDataReceive){
		requireNonNull(this.samePacketTimeDuration = samePacketTimeDuration);
		requireNonNull(this.onDataReceive = onDataReceive);
		requireNonNull(this.packetListReceiver = packetListReceiver);
	}

	@Override
	public void updateGarbledData() {
		instant = false;
		packetList.clear();
	}

	@Override
	public void update(Collection<? extends Packet> newPackets) {
		long nowNanos = System.nanoTime();
		boolean isFirstData = lastFirstReceivedDataNanos == null || nowNanos - lastFirstReceivedDataNanos > samePacketTimeDuration.toNanos();
		if (isFirstData) {
			lastFirstReceivedDataNanos = nowNanos; // set this to the first time we get bytes
		}
		onDataReceive.onDataReceive(isFirstData, instant ? InstantType.INSTANT : InstantType.NOT_INSTANT);
		packetList.addAll(newPackets);
	}

	@Override
	public void updateNoNewData() {
		long nowNanos = System.nanoTime();
		if (lastFirstReceivedDataNanos == null || nowNanos - lastFirstReceivedDataNanos > samePacketTimeDuration.toNanos()) { // if there's no new packets coming any time soon
			if (packetList.isEmpty()) {
				instant = true;
			} else {
				final boolean wasInstant = instant;
				instant = false;
				try {
					if (wasInstant) {
						packetListReceiver.receive(packetList, InstantType.INSTANT);
					} else {
						// In the future, we will remove the summary marker, but this has never had logging around it before,
						//   so we want to understand it better, hence the summary importance.
						LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Was going to send off " + packetList.size() + " packets to packetListReceiver, but instant=false!");
					}
				} finally {
					packetList.clear();
				}
			}
		}
	}
}
