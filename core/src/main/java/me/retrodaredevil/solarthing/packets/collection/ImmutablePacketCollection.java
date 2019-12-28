package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.*;

class ImmutablePacketCollection implements PacketCollection {
	private final List<Packet> packets;
	/** The UTC date represented in milliseconds */
	private final long dateMillis;
	/** A special field that's used when serializing an object into a couchdb database to uniquely identify it*/
	private final String _id;

	/**
	 * Creates a new PacketCollection
	 * <p>
	 * If packets is mutated after this constructor is called, it will have no effect; it is tolerated
	 * @param packets The packets collection
	 */
	ImmutablePacketCollection(Collection<? extends Packet> packets, PacketCollectionIdGenerator idGenerator){
		this.packets = Collections.unmodifiableList(new ArrayList<>(packets));
		final Calendar cal = new GregorianCalendar();
		dateMillis = cal.getTimeInMillis(); // in UTC
		this._id = idGenerator.generateId(cal);
	}
	// TODO We need a way for PacketCollections to be deserialized

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
		return _id;
	}
	
}
