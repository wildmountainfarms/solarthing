package me.retrodaredevil.solarthing.rest.cache.creators;

import me.retrodaredevil.solarthing.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.cache.packets.data.FXAccumulationDataCache;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.rest.cache.CacheCalc;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class FXAccumulationCacheNodeCreator implements IdentificationCacheNodeCreator<FXAccumulationDataCache, DailyFXPacket> {
	@Override
	public Class<DailyFXPacket> getAcceptedType() {
		return DailyFXPacket.class;
	}

	@Override
	public String getCacheName() {
		return FXAccumulationDataCache.CACHE_NAME;
	}

	@Override
	public IdentificationCacheNode<FXAccumulationDataCache> create(IdentifierFragment identifierFragment, List<TimestampedPacket<DailyFXPacket>> timestampedPackets, Instant periodStart, Duration periodDuration) {
		return CacheCalc.calculateCache(
				identifierFragment, timestampedPackets, periodStart, periodDuration,
				FXAccumulationDataCache::convert, FXAccumulationDataCache.getDataFactory(),
				data -> data, FXAccumulationDataCache::createFromIdentifier
		);
	}
}
