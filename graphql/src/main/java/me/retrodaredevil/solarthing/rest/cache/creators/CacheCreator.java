package me.retrodaredevil.solarthing.rest.cache.creators;

import me.retrodaredevil.solarthing.type.cache.packets.CacheDataPacket;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface CacheCreator {
	CacheDataPacket createFrom(String sourceId, List<InstancePacketGroup> packetGroups, Instant periodStart, Duration periodDuration);
}
