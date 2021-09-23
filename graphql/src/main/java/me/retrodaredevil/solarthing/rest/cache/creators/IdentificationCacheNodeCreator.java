package me.retrodaredevil.solarthing.rest.cache.creators;

import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.type.cache.packets.data.IdentificationCacheData;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface IdentificationCacheNodeCreator<T extends IdentificationCacheData, U extends Identifiable> {
	Class<U> getAcceptedType();
	String getCacheName();
	IdentificationCacheNode<T> create(IdentifierFragment identifierFragment, List<TimestampedPacket<U>> packets, Instant periodStart, Duration periodDuration);
}
