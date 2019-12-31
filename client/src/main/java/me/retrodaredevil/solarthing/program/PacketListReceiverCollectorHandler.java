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

public class PacketListReceiverCollectorHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketListReceiverCollectorHandler.class);
	private final PacketListReceiver packetListReceiver;
	private final PacketHandler packetHandler;

	private final List<Packet> packetList = new ArrayList<>();

	public PacketListReceiverCollectorHandler(PacketListReceiver packetListReceiver, PacketHandler packetHandler) {
		this.packetListReceiver = packetListReceiver;
		this.packetHandler = packetHandler;
	}

	public PacketListReceiver getPacketListReceiverAccepter(){
		return receiver;
	}
	private final PacketListReceiver receiver = (packets, wasInstant) -> packetList.addAll(packets);
	public PacketListReceiver getPacketListReceiverHandler(){
		return handler;
	}
	private final PacketListReceiver handler = (packets, wasInstant) -> handle(wasInstant);

	public void handle(boolean wasInstant) {
		if(packetList.isEmpty()){
			return;
		}
		packetListReceiver.receive(packetList, wasInstant);
		PacketCollection packetCollection = PacketCollections.createFromPackets(packetList, PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR);
		packetList.clear();
		try {
			packetHandler.handle(packetCollection, wasInstant);
		} catch (PacketHandleException e) {
			LOGGER.error("Couldn't handle packets!", e);
		}
	}
}
