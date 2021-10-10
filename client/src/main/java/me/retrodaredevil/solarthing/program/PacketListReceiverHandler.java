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

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class PacketListReceiverHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketListReceiverHandler.class);
	private final PacketListReceiver packetListReceiver;
	private final PacketHandler packetHandler;
	private final PacketCollectionIdGenerator idGenerator;
	private final ZoneId zoneId;

	private final List<Packet> packetList = new ArrayList<>();
	private final List<PacketCollection> packetCollectionList = new ArrayList<>();

	/**
	 *
	 * @param packetListReceiver The {@link PacketListReceiver} that is only used if the list of packets to pack is not empty
	 * @param packetHandler Handles packed packets
	 * @param idGenerator ID Generator for the packet collection
	 * @param zoneId The timezone used when generating packet collection IDs
	 */
	public PacketListReceiverHandler(PacketListReceiver packetListReceiver, PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, ZoneId zoneId) {
		this.packetListReceiver = packetListReceiver;
		this.packetHandler = packetHandler;
		this.idGenerator = idGenerator;
		this.zoneId = zoneId;
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
		Instant now = Instant.now();
		PacketCollection packetCollection = PacketCollections.createFromPackets(now, packetList, idGenerator, zoneId);
		packetList.clear();
		packetCollectionList.add(packetCollection);
	}

	public void handle(InstantType instantType) {
		for (PacketCollection packetCollection : packetCollectionList) {
			handleSinglePacketCollection(packetCollection, instantType);
		}
		packetCollectionList.clear();
	}
	private void handleSinglePacketCollection(PacketCollection packetCollection, InstantType instantType) {
		try {
			packetHandler.handle(packetCollection, instantType);
		} catch (PacketHandleException e) {
			LOGGER.error("Couldn't handle packet collection. id: " + packetCollection.getDbId() + " dateMillis: " + packetCollection.getDateMillis() + ". Will NOT try again.", e);
		}
	}

	/**
	 * This method is used to be able to upload packets in a simple way without dealing with {@link PacketListReceiver}s.
	 * <p>
	 * If {@code packets} is empty, this method does nothing. If not empty, it may add additional packets, turn into a {@link PacketCollection},
	 * then "handles" the {@link PacketCollection}, which usually means to persist the {@link PacketCollection} in a database.
	 * @param packets The packets to upload
	 */
	public void uploadSimple(Instant now, List<? extends Packet> packets) {
		List<Packet> mutablePackets = new ArrayList<>(packets);
		if (mutablePackets.isEmpty()) {
			return;
		}
		packetListReceiver.receive(mutablePackets, InstantType.INSTANT); // this call may mutate mutablePackets, which is why we need it in the first place
		PacketCollection packetCollection = PacketCollections.createFromPackets(now, mutablePackets, idGenerator, zoneId);
		handleSinglePacketCollection(packetCollection, InstantType.INSTANT);
	}
}
