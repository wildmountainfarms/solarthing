package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.*;

class ImmutablePacketCollection implements PacketCollection {
	private final List<Packet> packets;
	/** The UTC date represented in milliseconds */
	private final long dateMillis;
	/** Used to identify this packet collection. May be the same as slightly older {@link PacketCollection}s to indicate we should replace/update it */
	private final String id;

	/**
	 * Creates a new PacketCollection
	 * <p>
	 * If packets is mutated after this constructor is called, it will have no effect; it is tolerated
	 * @param packets The packets collection
	 */
	ImmutablePacketCollection(Collection<? extends Packet> packets, long dateMillis, String id){
		this.packets = Collections.unmodifiableList(new ArrayList<>(packets));
		this.dateMillis = dateMillis;
		this.id = id;
	}

	@Override
	public List<Packet> getPackets() {
		return packets;
	}

	@Override
	public long getDateMillis() {
		return dateMillis;
	}

	@Override
	public String getDbId() {
		return id;
	}

}
