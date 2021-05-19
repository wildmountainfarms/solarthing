package me.retrodaredevil.solarthing.rest.cache.creators;

import me.retrodaredevil.solarthing.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.rest.cache.CacheCalc;
import me.retrodaredevil.solarthing.solar.accumulation.value.FloatAccumulationValue;
import me.retrodaredevil.solarthing.solar.accumulation.value.FloatAccumulationValueFactory;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class ChargeControllerAccumulationCacheNodeCreator implements IdentificationCacheNodeCreator<ChargeControllerAccumulationDataCache, DailyChargeController> {
	@Override
	public Class<DailyChargeController> getAcceptedType() {
		return DailyChargeController.class;
	}

	@Override
	public String getCacheName() {
		return ChargeControllerAccumulationDataCache.CACHE_NAME;
	}

	@Override
	public IdentificationCacheNode<ChargeControllerAccumulationDataCache> create(IdentifierFragment identifierFragment, List<TimestampedPacket<DailyChargeController>> timestampedPackets, Instant periodStart, Duration periodDuration) {
		return CacheCalc.calculateCache(
				identifierFragment, timestampedPackets, periodStart, periodDuration,
				FloatAccumulationValue.convert(DailyChargeController::getDailyKWH), FloatAccumulationValueFactory.getInstance(),
				FloatAccumulationValue::getValue, ChargeControllerAccumulationDataCache::new
		);
	}
}
