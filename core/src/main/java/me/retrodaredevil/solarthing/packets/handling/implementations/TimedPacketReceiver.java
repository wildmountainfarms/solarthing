package me.retrodaredevil.solarthing.packets.handling.implementations;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.RawPacketReceiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class TimedPacketReceiver implements RawPacketReceiver {

	private final long samePacketTime;
	private final PacketListReceiver packetListReceiver;
	private final OnDataReceive onDataReceive;

	private long lastFirstReceivedData = Long.MIN_VALUE; // the last time a packet was added to packetList

	/** A list that piles up packets and handles when needed. May be cleared */
	private final List<Packet> packetList = new ArrayList<>(); //
	private boolean instant = false;

	/**
	 *  @param samePacketTime The maximum amount of time allowed between packets that will be grouped together in a {@link me.retrodaredevil.solarthing.packets.collection.PacketCollection}
	 * @param packetListReceiver A {@link PacketListReceiver} which adds additional packets before saving
	 * @param onDataReceive This is called whenever data is received from {@code in}
	 */
	public TimedPacketReceiver(long samePacketTime, PacketListReceiver packetListReceiver, OnDataReceive onDataReceive){
		this.samePacketTime = samePacketTime;
		this.onDataReceive = requireNonNull(onDataReceive);
		this.packetListReceiver = requireNonNull(packetListReceiver);
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
		onDataReceive.onDataReceive(firstData, instant ? InstantType.INSTANT : InstantType.NOT_INSTANT);
		packetList.addAll(newPackets);
	}

	@Override
	public void updateNoNewData() {
		long now = System.currentTimeMillis();
		if (lastFirstReceivedData + samePacketTime < now) { // if there's no new packets coming any time soon
			if (packetList.isEmpty()) {
				instant = true;
			} else {
				final InstantType instantType = instant ? InstantType.INSTANT : InstantType.NOT_INSTANT;
				instant = false;
				try {
					packetListReceiver.receive(packetList, instantType);
				} finally {
					packetList.clear();
				}
			}
		}
	}
}
