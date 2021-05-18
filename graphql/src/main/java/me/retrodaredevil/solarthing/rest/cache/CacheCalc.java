package me.retrodaredevil.solarthing.rest.cache;

import me.retrodaredevil.solarthing.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.cache.packets.data.IdentificationCacheData;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.solar.accumulation.*;
import me.retrodaredevil.solarthing.solar.accumulation.value.AccumulationValue;
import me.retrodaredevil.solarthing.solar.accumulation.value.AccumulationValueFactory;
import me.retrodaredevil.solarthing.solar.common.DailyData;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

public final class CacheCalc {
	private CacheCalc() { throw new UnsupportedOperationException(); }

	public static <T extends IdentificationCacheData, U extends DailyData, V extends AccumulationValue<V>, W> IdentificationCacheNode<T> calculateCache(
			IdentifierFragment identifierFragment, List<TimestampedPacket<U>> timestampedPackets, Instant periodStart, Duration periodDuration,
			TotalGetter<U, V> totalGetter,
			AccumulationValueFactory<V> accumulationValueFactory, Function<V, W> converter, DataCreator<T, W> dataCreator) {

		AccumulationConfig accumulationConfig = AccumulationConfig.createDefault(periodStart.toEpochMilli());
		List<AccumulationPair<U>> accumulationPairs = AccumulationUtil.getAccumulationPairs(timestampedPackets, accumulationConfig);
		List<AccumulationCalc.SumNode<V>> sumNodes = AccumulationCalc.getTotals(accumulationPairs, totalGetter, timestampedPackets, accumulationValueFactory);
		long periodStartDateMillis = periodStart.toEpochMilli();
		long previousPeriodStartDateMillis = periodStartDateMillis - periodDuration.toMillis();
		long unknownCutOffDateMillis = periodStartDateMillis - CacheHandler.INFO_DURATION.toMillis();
		long periodEndDateMillis = periodStart.toEpochMilli() + periodDuration.toMillis();
		AccumulationCalc.SumNode<V> lastDataBeforePreviousPeriodStart = null;
		AccumulationCalc.SumNode<V> lastDataBeforePeriodStart = null;
		AccumulationCalc.SumNode<V> firstDataAfterPeriodStart = null;
		AccumulationCalc.SumNode<V> lastDataBeforePeriodEnd = null;
		for (AccumulationCalc.SumNode<V> sumNode : sumNodes) {
			long dateMillis = sumNode.getDateMillis();
			if (dateMillis >= periodEndDateMillis) {
				break;
			}
			if (dateMillis < unknownCutOffDateMillis) {
				// If we have data before this, we need to not use it because using it could yield different results depending on the amount of data
				//   we have available to use -- We don't want that because the result of calculating a cache should be the same every time.
				continue;
			}
			if (dateMillis < previousPeriodStartDateMillis) {
				lastDataBeforePreviousPeriodStart = sumNode;
			} else if (dateMillis < periodStartDateMillis) {
				lastDataBeforePeriodStart = sumNode;
			} else {
				if (firstDataAfterPeriodStart == null) {
					firstDataAfterPeriodStart = sumNode;
				}
				lastDataBeforePeriodEnd = sumNode;
			}
		}
		final T data;
		if (firstDataAfterPeriodStart == null) {
			assert lastDataBeforePeriodEnd == null;

			data = dataCreator.create(
					identifierFragment.getIdentifier(),
					converter.apply(accumulationValueFactory.getZero()),
					null, null,
					converter.apply(accumulationValueFactory.getZero()),
					null
			);
		} else {
			AccumulationCalc.SumNode<V> firstData = lastDataBeforePeriodStart == null ? firstDataAfterPeriodStart : lastDataBeforePeriodStart;
			V generation = lastDataBeforePeriodEnd.getSum().minus(firstData.getSum());
			final V unknownGeneration;
			final Long unknownStartDateMillis;
			if (lastDataBeforePeriodStart == null && lastDataBeforePreviousPeriodStart != null) {
				unknownGeneration = firstDataAfterPeriodStart.getSum().minus(lastDataBeforePreviousPeriodStart.getSum());
				unknownStartDateMillis = lastDataBeforePreviousPeriodStart.getDateMillis();
			} else {
				unknownGeneration = accumulationValueFactory.getZero();
				unknownStartDateMillis = null;
			}
			data = dataCreator.create(
					identifierFragment.getIdentifier(),
					converter.apply(generation),
					firstData.getDateMillis(), lastDataBeforePeriodEnd.getDateMillis(),
					converter.apply(unknownGeneration),
					unknownStartDateMillis
			);
		}
		return new IdentificationCacheNode<>(identifierFragment.getFragmentId(), data);
	}

	public interface DataCreator<T, U> {
		T create(Identifier identifier, U mainData, Long firstDateMillis, Long lastDateMillis, U unknownData, Long unknownStartDateMillis);
	}
}
