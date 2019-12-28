package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;

public final class PacketCollections {
	private PacketCollections(){ throw new UnsupportedOperationException(); }

	public static PacketCollection createFromPackets(Collection<Packet> packets, PacketCollectionIdGenerator idGenerator){
		return new ImmutablePacketCollection(packets, idGenerator);
	}
}
