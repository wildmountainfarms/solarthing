package me.retrodaredevil.solarthing.rest.cache.creators;

import me.retrodaredevil.solarthing.type.cache.packets.CacheDataPacket;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface CacheCreator {
	/**
	 * Creates a {@link CacheDataPacket} to be stored in the database for a given period.
	 * @param sourceId The source ID
	 * @param packetGroups The queried packet groups. Note that packets here may and will be out of the range of the current period.
	 * @param periodStart The start of the period
	 * @param periodDuration The duration of the period
	 * @return The created packet
	 */
	CacheDataPacket createFrom(String sourceId, List<InstancePacketGroup> packetGroups, Instant periodStart, Duration periodDuration);
}
