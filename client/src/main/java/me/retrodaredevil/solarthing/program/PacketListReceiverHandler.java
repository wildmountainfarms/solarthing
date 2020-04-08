package me.retrodaredevil.solarthing.program;

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

public class PacketListReceiverHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketListReceiverHandler.class);
	private final PacketListReceiver packetListReceiver;
	private final PacketHandler packetHandler;
	private final PacketCollectionIdGenerator idGenerator;

	private final List<Packet> packetList = new ArrayList<>();
	private final List<PacketCollection> packetCollectionList = new ArrayList<>();

	public PacketListReceiverHandler(PacketListReceiver packetListReceiver, PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator) {
		this.packetListReceiver = packetListReceiver;
		this.packetHandler = packetHandler;
		this.idGenerator = idGenerator;
	}

	/**
	 *
	 * @return The {@link PacketListReceiver} to accept the packets. When this is used, packets are accepted and stored
	 */
	public PacketListReceiver getPacketListReceiverAccepter(){
		return receiver;
	}
	private final PacketListReceiver receiver = (packets, wasInstant) -> packetList.addAll(packets);

	/**
	 * NOTE: This does not do anything with the packets passed to {@link PacketListReceiver#receive(List, boolean)}.
	 * @return The {@link PacketListReceiver} that uses the packets currently stored to create a {@link PacketCollection} that can be handled with returned value from {@link #getPacketListReceiverHandler()}
	 */
	public PacketListReceiver getPacketListReceiverPacker(){
		return packer;
	}
	private final PacketListReceiver packer = (packets, wasInstant) -> pack(wasInstant);

	/**
	 * NOTE: This does not do anything with the packets passed to {@link PacketListReceiver#receive(List, boolean)}.
	 * @return A {@link PacketListReceiver} that handles the {@link PacketCollection}s that are currently stored
	 */
	public PacketListReceiver getPacketListReceiverHandler(){
		return handler;
	}
	private final PacketListReceiver handler = (packets, wasInstant) -> handle(wasInstant);

	public void pack(boolean wasInstant){
		if(packetList.isEmpty()){
			return;
		}
		packetListReceiver.receive(packetList, wasInstant);
		PacketCollection packetCollection = PacketCollections.createFromPackets(packetList, idGenerator);
		packetList.clear();
		packetCollectionList.add(packetCollection);
	}

	public void handle(boolean wasInstant) {
		for (PacketCollection packetCollection : packetCollectionList) {
			try {
				packetHandler.handle(packetCollection, wasInstant);
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't packet collection id: " + packetCollection.getDbId() + " dateMillis: " + packetCollection.getDateMillis(), ". Will NOT try again.", e);
			}
		}
		packetCollectionList.clear();
	}
}
