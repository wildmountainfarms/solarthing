package me.retrodaredevil.iot.packets;


import java.util.List;

public interface PacketCollection extends PacketEntry {
	/**
	 * Should be serialized as "packets"
	 * @return An unmodifiable list of packets
	 */
	List<? extends Packet> getPackets();

	/**
	 * Should be serialized as "dateArray"
	 * NOTE: Modifying this may mutate this object. DO NOT MODIFY
	 * @return The date array representing the local time. [year, month, day, hour, minute, second, millisecond]
	 */
	int[] getDateArray();

	/**
	 * Should be serialized as "dateMillis"
	 * @return The date this packet collection was created in milliseconds (UTC)
	 */
	long getDateMillis();

}
