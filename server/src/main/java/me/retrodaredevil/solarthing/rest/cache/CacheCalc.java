package me.retrodaredevil.solarthing.rest.cache;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.type.cache.packets.data.IdentificationCacheData;
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

@UtilityClass
public final class CacheCalc {
	private CacheCalc() { throw new UnsupportedOperationException(); }

	/**
	 * Ok, so this function may seem complex, and yeah, it is. The purpose of this is to find the total accumulation over a period of time.
	 * This is designed with generics to be able to used for floats, or data types containing multiple floats, but can also work with other data
	 * types that can be added or subtracted. The 4 generic type parameters have been given meaningful names to prevent confusion.
	 *
	 * @param identifierFragment The {@link IdentifierFragment}
	 * @param timestampedPackets The list of timestamped packets
	 * @param periodStart The start of the period
	 * @param periodDuration The duration of the period
	 * @param totalGetter Contains a function to get the desired data from a packet
	 * @param accumulationValueFactory The factory for the {@link AccumulationValue} being used
	 * @param converter A function to convert the {@link AccumulationValue} to the type used to create the {@link IdentificationCacheData}
	 * @param dataCreator Contains a function to create the {@link IdentificationCacheData}
	 * @param <DATA> The type data that will be cached
	 * @param <PACKET> The type of packet that will be used to calculate the cache
	 * @param <VALUE> The type of {@link AccumulationValue} that is used to accumulate the data
	 * @param <ACCEPTED_VALUE> The type that {@code dataCreator} requires to create a {@code DATA} type.
	 * @return A new {@link IdentificationCacheData} with data created using {@code dataCreator}
	 */
	public static <DATA extends IdentificationCacheData, PACKET extends DailyData, VALUE extends AccumulationValue<VALUE>, ACCEPTED_VALUE> IdentificationCacheNode<DATA> calculateCache(
			IdentifierFragment identifierFragment, List<TimestampedPacket<PACKET>> timestampedPackets, Instant periodStart, Duration periodDuration,
			TotalGetter<PACKET, VALUE> totalGetter,
			AccumulationValueFactory<VALUE> accumulationValueFactory, Function<VALUE, ACCEPTED_VALUE> converter, DataCreator<DATA, ACCEPTED_VALUE> dataCreator) {

		AccumulationConfig accumulationConfig = AccumulationConfig.createDefault(periodStart.toEpochMilli());
		List<AccumulationPair<PACKET>> accumulationPairs = AccumulationUtil.getAccumulationPairs(timestampedPackets, accumulationConfig);
		List<AccumulationCalc.SumNode<VALUE>> sumNodes = AccumulationCalc.getTotals(accumulationPairs, totalGetter, timestampedPackets, accumulationValueFactory);
		long periodStartDateMillis = periodStart.toEpochMilli();
		long previousPeriodStartDateMillis = periodStartDateMillis - periodDuration.toMillis();
		long unknownCutOffDateMillis = periodStartDateMillis - CacheHandler.INFO_DURATION.toMillis();
		long periodEndDateMillis = periodStart.toEpochMilli() + periodDuration.toMillis();
		AccumulationCalc.SumNode<VALUE> lastDataBeforePreviousPeriodStart = null;
		AccumulationCalc.SumNode<VALUE> lastDataBeforePeriodStart = null;
		AccumulationCalc.SumNode<VALUE> firstDataAfterPeriodStart = null;
		AccumulationCalc.SumNode<VALUE> lastDataBeforePeriodEnd = null;
		for (AccumulationCalc.SumNode<VALUE> sumNode : sumNodes) {
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
		final DATA data;
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
			AccumulationCalc.SumNode<VALUE> firstData = lastDataBeforePeriodStart == null ? firstDataAfterPeriodStart : lastDataBeforePeriodStart;
			VALUE generation = lastDataBeforePeriodEnd.getSum().minus(firstData.getSum());
			final VALUE unknownGeneration;
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

	@FunctionalInterface
	public interface DataCreator<T, U> {
		T create(Identifier identifier, U mainData, Long firstDateMillis, Long lastDateMillis, U unknownData, Long unknownStartDateMillis);
	}
}
