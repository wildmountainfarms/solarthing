package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class PacketListReceiverHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketListReceiverHandler.class);
	private final PacketListReceiver packetListReceiver;
	private final PacketHandler packetHandler;
	private final PacketCollectionIdGenerator idGenerator;
	private final TimeZone timeZone;

	private final List<Packet> packetList = new ArrayList<>();
	private final List<PacketCollection> packetCollectionList = new ArrayList<>();

	/**
	 *
	 * @param packetListReceiver The {@link PacketListReceiver} that is only used if the list of packets to pack is not empty
	 * @param packetHandler Handles packed packets
	 * @param idGenerator ID Generator for the packet collection
	 * @param timeZone The timezone used when generating packet collection IDs
	 */
	public PacketListReceiverHandler(PacketListReceiver packetListReceiver, PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, TimeZone timeZone) {
		this.packetListReceiver = packetListReceiver;
		this.packetHandler = packetHandler;
		this.idGenerator = idGenerator;
		this.timeZone = timeZone;
	}

	/**
	 *
	 * @return The {@link PacketListReceiver} to accept the packets. When this is used, packets are accepted and stored
	 */
	public PacketListReceiver getPacketListReceiverAccepter(){
		return receiver;
	}
	private final PacketListReceiver receiver = (packets, instantType) -> packetList.addAll(packets);

	/**
	 * NOTE: This does not do anything with the packets passed to {@link PacketListReceiver#receive(List, InstantType)}.
	 * @return The {@link PacketListReceiver} that uses the packets currently stored to create a {@link PacketCollection} that can be handled with returned value from {@link #getPacketListReceiverHandler()}
	 */
	public PacketListReceiver getPacketListReceiverPacker(){
		return packer;
	}
	private final PacketListReceiver packer = (packets, instantType) -> pack(instantType);

	/**
	 * NOTE: This does not do anything with the packets passed to {@link PacketListReceiver#receive(List, InstantType)}.
	 * @return A {@link PacketListReceiver} that handles the {@link PacketCollection}s that are currently stored
	 */
	public PacketListReceiver getPacketListReceiverHandler(){
		return handler;
	}
	private final PacketListReceiver handler = (packets, instantType) -> handle(instantType);

	public void pack(InstantType instantType){
		if(packetList.isEmpty()){
			return;
		}
		packetListReceiver.receive(packetList, instantType);
		PacketCollection packetCollection = PacketCollections.createFromPackets(packetList, idGenerator, timeZone);
		packetList.clear();
		packetCollectionList.add(packetCollection);
	}

	public void handle(InstantType instantType) {
		for (PacketCollection packetCollection : packetCollectionList) {
			try {
				packetHandler.handle(packetCollection, instantType);
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't handle packet collection. id: " + packetCollection.getDbId() + " dateMillis: " + packetCollection.getDateMillis() + ". Will NOT try again.", e);
			}
		}
		packetCollectionList.clear();
	}
}
