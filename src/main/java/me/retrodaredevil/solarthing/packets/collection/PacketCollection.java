package me.retrodaredevil.solarthing.packets.collection;


import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.PacketEntry;

import java.util.List;

public interface PacketCollection extends PacketEntry {
	/**
	 * Should be serialized as "packets"
	 * @return An unmodifiable list of packets
	 */
	List<? extends Packet> getPackets();

	/**
	 * Should be serialized as "dateArray"
	 * <p>
	 * NOTE: Modifying this may mutate this object. DO NOT MODIFY
	 * <p>
	 * NOTE: The month is 1 index based (range of [1..12]). The day of the month is also 1 index based (standard)
	 * @return The date array representing the local time. [year, month, day, hour, minute, second, millisecond]
	 */
	@Deprecated
	int[] getDateArray();

	/**
	 * Should be serialized as "dateMillis"
	 * @return The date this packet collection was created in milliseconds (UTC)
	 */
	long getDateMillis();

}
