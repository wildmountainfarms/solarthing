package me.retrodaredevil.solarthing.rest.cache.creators;

import me.retrodaredevil.solarthing.type.cache.packets.DefaultIdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.type.cache.packets.data.IdentificationCacheData;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * This class represents the simple case where creating a {@link IdentificationCacheDataPacket} requires a single type of packet.
 *
 * @param <T> The type of data inside each node that will be a part of the produced {@link IdentificationCacheDataPacket}
 * @param <U> The type required by the given {@link IdentificationCacheNodeCreator} that will filter packets necessary to create a given node
 */
public class DefaultIdentificationCacheCreator<T extends IdentificationCacheData, U extends Identifiable> implements CacheCreator {
	private final IdentificationCacheNodeCreator<T, U> identificationCacheNodeCreator;

	public DefaultIdentificationCacheCreator(IdentificationCacheNodeCreator<T, U> identificationCacheNodeCreator) {
		requireNonNull(this.identificationCacheNodeCreator = identificationCacheNodeCreator);
	}

	@Override
	public IdentificationCacheDataPacket<T> createFrom(String sourceId, List<InstancePacketGroup> packetGroups, Instant periodStart, Duration periodDuration) {
		Class<U> acceptedType = identificationCacheNodeCreator.getAcceptedType();
		Map<String, Map<IdentifierFragment, List<TimestampedPacket<U>>>> mappedPackets = convertPackets(packetGroups, acceptedType);
		Map<IdentifierFragment, List<TimestampedPacket<U>>> identifierFragmentMap = mappedPackets.getOrDefault(sourceId, Collections.emptyMap());

		List<IdentificationCacheNode<T>> nodes = new ArrayList<>(identifierFragmentMap.size());
		for (Map.Entry<IdentifierFragment, List<TimestampedPacket<U>>> entry : identifierFragmentMap.entrySet()) {
			IdentifierFragment identifierFragment = entry.getKey();
			List<TimestampedPacket<U>> packets = entry.getValue();
			IdentificationCacheNode<T> node = identificationCacheNodeCreator.create(identifierFragment, packets, periodStart, periodDuration);
			nodes.add(node);
		}
		return new DefaultIdentificationCacheDataPacket<>(
				periodStart.toEpochMilli(),
				periodDuration.toMillis(),
				sourceId,
				identificationCacheNodeCreator.getCacheName(),
				nodes
		);
	}
	@SuppressWarnings("unchecked")
	private static <T extends Identifiable> Map<String, Map<IdentifierFragment, List<TimestampedPacket<T>>>> convertPackets(List<? extends FragmentedPacketGroup> packetGroups, Class<T> acceptedType) {
		Map<String, Map<IdentifierFragment, List<TimestampedPacket<T>>>> r = new HashMap<>();
		for (FragmentedPacketGroup packetGroup : packetGroups) {
			for (Packet packet : packetGroup.getPackets()) {
				if (!acceptedType.isInstance(packet)) {
					continue;
				}
				T identifiablePacket = (T) packet;
				String sourceId = packetGroup.getSourceId(packet);
				int fragmentId = packetGroup.getFragmentId(packet);
				Identifier identifier = identifiablePacket.getIdentifier();
				IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, identifier);
				List<TimestampedPacket<T>> list = r
						.computeIfAbsent(sourceId, k -> new HashMap<>())
						.computeIfAbsent(identifierFragment, k -> new ArrayList<>());
				long dateMillis = packetGroup.getDateMillisOrKnown(packet);
				list.add(new TimestampedPacket<>(identifiablePacket, dateMillis));
			}
		}
		return r;
	}
}
