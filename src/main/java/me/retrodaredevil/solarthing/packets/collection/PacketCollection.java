package me.retrodaredevil.solarthing.packets.collection;


import me.retrodaredevil.solarthing.packets.PacketEntry;

public interface PacketCollection extends PacketGroup, PacketEntry {
	/**
	 * Should be serialized as "dateArray"
	 * <p>
	 * NOTE: Modifying this may mutate this object. DO NOT MODIFY
	 * <p>
	 * NOTE: The month is 1 index based (range of [1..12]). The day of the month is also 1 index based (standard)
	 * @return The date array representing the local time. [year, month, day, hour, minute, second, millisecond]. Or null if not supported
	 */
	@Deprecated
	default int[] getDateArray(){ return null; }
	

}
