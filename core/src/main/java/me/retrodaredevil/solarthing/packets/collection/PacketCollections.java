package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

public final class PacketCollections {
	private PacketCollections(){ throw new UnsupportedOperationException(); }

	public static PacketCollection createFromPackets(Collection<? extends Packet> packets, PacketCollectionIdGenerator idGenerator){
		final Calendar cal = new GregorianCalendar();
		long dateMillis = cal.getTimeInMillis(); // in UTC
		String id = idGenerator.generateId(cal);
		return new ImmutablePacketCollection(packets, dateMillis, id);
	}
}
