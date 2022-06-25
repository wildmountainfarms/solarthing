package me.retrodaredevil.solarthing.type.cache.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.type.cache.packets.data.IdentificationCacheData;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;

import java.util.*;
import java.util.stream.Collectors;

public final class DefaultIdentificationCacheDataPacket<T extends IdentificationCacheData> extends BaseCacheDataPacket implements IdentificationCacheDataPacket<T> {
	private final List<IdentificationCacheNode<T>> nodes;
	@JsonCreator
	public DefaultIdentificationCacheDataPacket(
			@JsonProperty("periodStartDateMillis") long periodStartDateMillis,
			@JsonProperty("periodDurationMillis") long periodDurationMillis,
			@JsonProperty("sourceId") String sourceId,
			@JsonProperty("cacheName") String cacheName,
			@JsonProperty("nodes") List<IdentificationCacheNode<T>> nodes) {
		super(periodStartDateMillis, periodDurationMillis, sourceId, cacheName);
		this.nodes = nodes;
	}

	@Override
	public List<IdentificationCacheNode<T>> getNodes() {
		return nodes;
	}

	private static <T extends IdentificationCacheData> void putOnMap(Map<IdentifierFragment, T> map, List<IdentificationCacheNode<T>> list) {
		for (IdentificationCacheNode<T> node : list) {
			IdentifierFragment identifierFragment = IdentifierFragment.create(node.getFragmentId(), node.getData().getIdentifier());
			T currentData = map.get(identifierFragment);
			if (currentData != null) {
				@SuppressWarnings("unchecked")
				T data = (T) currentData.combine(node.getData());
				map.put(identifierFragment, data);
			} else {
				map.put(identifierFragment, node.getData());
			}
		}
	}

	private static <T extends IdentificationCacheData> List<IdentificationCacheNode<T>> combineNodes(List<IdentificationCacheNode<T>> first, List<IdentificationCacheNode<T>> second) {
		Map<IdentifierFragment, T> map = new HashMap<>();
		putOnMap(map, first);
		putOnMap(map, second);
		return map.entrySet().stream().map((identifierFragmentTEntry) -> {
			IdentifierFragment identifierFragment = identifierFragmentTEntry.getKey();
			T data = identifierFragmentTEntry.getValue();
			return new IdentificationCacheNode<>(identifierFragment.getFragmentId(), data);
		}).collect(Collectors.toList());
	}

	@Override
	public IdentificationCacheDataPacket<T> combine(CacheDataPacket futurePacket) {
		@SuppressWarnings("unchecked")
		DefaultIdentificationCacheDataPacket<T> packet = (DefaultIdentificationCacheDataPacket<T>) futurePacket;
		return new DefaultIdentificationCacheDataPacket<>(
				getPeriodStartDateMillis(),
				packet.getPeriodEndDateMillis() - getPeriodStartDateMillis(),
				getSourceId(),
				getCacheName(),
				combineNodes(getNodes(), packet.getNodes())
		);
	}
}
