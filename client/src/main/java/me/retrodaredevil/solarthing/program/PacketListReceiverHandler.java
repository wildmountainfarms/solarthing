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
import java.util.Iterator;
import java.util.List;

public class PacketListReceiverHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketListReceiverHandler.class);
	private final PacketListReceiver packetListReceiver;
	private final PacketHandler packetHandler;
	private final PacketCollectionIdGenerator idGenerator;
	private final int packetCollectionsToKeepOnFail;

	private final List<Packet> packetList = new ArrayList<>();
	private final List<PacketCollection> packetCollectionList = new ArrayList<>();

	public PacketListReceiverHandler(PacketListReceiver packetListReceiver, PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, int packetCollectionsToKeepOnFail) {
		this.packetListReceiver = packetListReceiver;
		this.packetHandler = packetHandler;
		this.idGenerator = idGenerator;
		this.packetCollectionsToKeepOnFail = packetCollectionsToKeepOnFail;
		if(packetCollectionsToKeepOnFail < 0){
			throw new IllegalArgumentException("packetsToKeep=" + packetCollectionsToKeepOnFail);
		}
	}

	public PacketListReceiver getPacketListReceiverAccepter(){
		return receiver;
	}
	private final PacketListReceiver receiver = (packets, wasInstant) -> packetList.addAll(packets);

	public PacketListReceiver getPacketListReceiverPacker(){
		return packer;
	}
	private final PacketListReceiver packer = (packets, wasInstant) -> pack(wasInstant);

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
		for (Iterator<PacketCollection> iterator = packetCollectionList.iterator(); iterator.hasNext(); ) {
			PacketCollection packetCollection = iterator.next();
			try {
				packetHandler.handle(packetCollection, wasInstant);
				iterator.remove();
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't handle packets!", e);
				if(packetCollectionsToKeepOnFail == 0){
					iterator.remove();
				}
			}
		}
		while(packetCollectionList.size() > packetCollectionsToKeepOnFail){
			packetCollectionList.remove(0); // remove oldest
		}
	}
}
