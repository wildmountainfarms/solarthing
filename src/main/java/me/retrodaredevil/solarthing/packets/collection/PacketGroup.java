package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.List;

public interface PacketGroup {
	/**
	 * Should be serialized as "packets"
	 * @return An unmodifiable list of packets
	 */
	List<? extends Packet> getPackets();
	
	/**
	 * Should be serialized as "dateMillis"
	 * @return The date this packet collection was created in milliseconds (UTC)
	 */
	long getDateMillis();
	
	/**
	 * @param packet The packet to get the date millis from
	 * @return The date millis of the specific packet or null
	 * @throws java.util.NoSuchElementException If {@code packet} is not in {@link #getPackets()}. Optional to be thrown
	 */
	default Long getDateMillis(Packet packet){
		return null;
	}
}
