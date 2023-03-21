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

	/**
	 * Creates an {@link IdentificationCacheNode} that contains data calculated from the given packets.
	 *
	 * @param identifierFragment The identifier fragment for the identifier of each packet
	 * @param packets The packets of the generic type. Note that these packets may and will be out of the range of the given period. The implementation should filter for the given period
	 *                or use the extra data for *smart* calculations. You can assume these are sorted in ascending order.
	 * @param periodStart The start of the period
	 * @param periodDuration The duration of the period
	 */
	IdentificationCacheNode<T> create(IdentifierFragment identifierFragment, List<TimestampedPacket<U>> packets, Instant periodStart, Duration periodDuration);
}
