package me.retrodaredevil.solarthing.rest.cache;

import me.retrodaredevil.solarthing.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationCalc;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationConfig;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationPair;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationUtil;
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
		AccumulationConfig accumulationConfig = AccumulationConfig.createDefault(periodStart.toEpochMilli());
		List<AccumulationPair<DailyChargeController>> accumulationPairs = AccumulationUtil.getAccumulationPairs(timestampedPackets, accumulationConfig);
		List<AccumulationCalc.SumNode> sumNodes = AccumulationCalc.getTotals(accumulationPairs, DailyChargeController::getDailyKWH, timestampedPackets);
		long periodStartDateMillis = periodStart.toEpochMilli();
		long unknownCutOffDateMillis = periodStartDateMillis - CacheHandler.INFO_DURATION.toMillis();
		long periodEndDateMillis = periodStart.toEpochMilli() + periodDuration.toMillis();
		AccumulationCalc.SumNode lastDataBeforeCutOff = null;
		AccumulationCalc.SumNode lastDataBeforePeriodStart = null;
		AccumulationCalc.SumNode firstDataAfterPeriodStart = null;
		AccumulationCalc.SumNode lastDataBeforePeriodEnd = null;
		for (AccumulationCalc.SumNode sumNode : sumNodes) {
			long dateMillis = sumNode.getDateMillis();
			if (dateMillis >= periodEndDateMillis) {
				break;
			}
			if (dateMillis < unknownCutOffDateMillis) {
				lastDataBeforeCutOff = sumNode;
			} else if (dateMillis < periodStartDateMillis) {
				lastDataBeforePeriodStart = sumNode;
			} else {
				if (firstDataAfterPeriodStart == null) {
					firstDataAfterPeriodStart = sumNode;
				}
				lastDataBeforePeriodEnd = sumNode;
			}
		}
		final ChargeControllerAccumulationDataCache data;
		if (firstDataAfterPeriodStart == null) {
			assert lastDataBeforePeriodEnd == null;

			data = new ChargeControllerAccumulationDataCache(
					identifierFragment.getIdentifier(),
					0.0f,
					null, null,
					0.0f,
					null
			);
		} else {
			AccumulationCalc.SumNode firstData = lastDataBeforePeriodStart == null ? firstDataAfterPeriodStart : lastDataBeforePeriodStart;
			float generationKWH = lastDataBeforePeriodEnd.getSum() - firstData.getSum();
			final float unknownGenerationKWH;
			final Long unknownStartDateMillis;
			if (lastDataBeforePeriodStart == null && lastDataBeforeCutOff != null) {
				unknownGenerationKWH = firstDataAfterPeriodStart.getSum() - lastDataBeforeCutOff.getSum();
				unknownStartDateMillis = lastDataBeforeCutOff.getDateMillis();
			} else {
				unknownGenerationKWH = 0.0f;
				unknownStartDateMillis = null;
			}
			data = new ChargeControllerAccumulationDataCache(
					identifierFragment.getIdentifier(),
					generationKWH,
					firstData.getDateMillis(), lastDataBeforePeriodEnd.getDateMillis(),
					unknownGenerationKWH,
					unknownStartDateMillis
			);
		}
		return new IdentificationCacheNode<>(identifierFragment.getFragmentId(), data);
	}
}
