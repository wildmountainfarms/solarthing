package me.retrodaredevil.solarthing.packets.instance;

/**
 * This packet is used to indicate that multiple {@link me.retrodaredevil.solarthing.packets.collection.PacketCollection}s
 * in a database are fragmented. This means that packet collection A (fragment id of 1) and packet collection B (fragment id of 2)
 * should be treated as one packet IF a particular B packet collection is the closest B packet collection to packet collection A.
 * <p>
 * NOTE: This packet should be supplemented by a {@link InstanceSourcePacket} in the same packet collection
 */
public interface InstanceFragmentIndicatorPacket extends InstancePacket {
	/**
	 * Should be serialized as "fragmentId"
	 * @return The fragment id. The lowest fragment id is the "master" fragment packet collection
	 */
	int getFragmentId();
}
