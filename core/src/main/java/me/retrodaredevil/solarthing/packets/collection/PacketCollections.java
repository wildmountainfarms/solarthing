package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Packet;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;

@UtilityClass
public final class PacketCollections {
	private PacketCollections(){ throw new UnsupportedOperationException(); }

	public static PacketCollection createFromPackets(Instant now, Collection<? extends Packet> packets, PacketCollectionIdGenerator idGenerator, ZoneId zoneId){
		String id = idGenerator.generateId(now.atZone(zoneId));
		return create(now, packets, id);
	}
	public static PacketCollection create(Instant now, Collection<? extends Packet> packets, String id){
		return new ImmutablePacketCollection(packets, now.toEpochMilli(), id);
	}
}
