package me.retrodaredevil.solarthing.packets.handling.implementations;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TimeZone;

public class PacketHandlerPacketListReceiver implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandlerPacketListReceiver.class);

	private final PacketHandler packetHandler;
	private final PacketCollectionIdGenerator idGenerator;
	private final TimeZone timeZone;

	public PacketHandlerPacketListReceiver(PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, TimeZone timeZone) {
		this.packetHandler = packetHandler;
		this.idGenerator = idGenerator;
		this.timeZone = timeZone;
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		try {
			packetHandler.handle(PacketCollections.createFromPackets(packets, idGenerator, timeZone), instantType);
		} catch (PacketHandleException e) {
			LOGGER.error("Was unable to handle " + packets.size() + " packet(s)!", e);
		}
	}
}
