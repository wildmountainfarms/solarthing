package me.retrodaredevil.solarthing.cache.packets;

import me.retrodaredevil.solarthing.packets.PacketEntry;

public interface CachePacket<T extends CacheDataPacket> extends PacketEntry {
	long getStartTime();
	long getEndTime();

	long getDurationMillis();

	T getData();
}
