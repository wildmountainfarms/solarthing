package me.retrodaredevil.solarthing.cache.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.cache.packets.data.IdentificationCacheData;

import java.util.List;

public class DefaultIdentificationCacheDataPacket<T extends IdentificationCacheData> extends BaseCacheDataPacket implements IdentificationCacheDataPacket<T> {
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
}
