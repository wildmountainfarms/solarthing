package me.retrodaredevil.solarthing.rest.cache.creators;

import me.retrodaredevil.solarthing.misc.weather.TemperaturePacket;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.rest.cache.CacheHandler;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.type.cache.packets.data.BatteryRecordDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.TemperatureRecordDataCache;
import me.retrodaredevil.solarthing.util.integration.MutableIntegral;
import me.retrodaredevil.solarthing.util.integration.TrapezoidalRuleAccumulator;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class TemperatureRecordCacheNodeCreator implements IdentificationCacheNodeCreator<TemperatureRecordDataCache, TemperaturePacket> {
	@Override
	public Class<TemperaturePacket> getAcceptedType() {
		return TemperaturePacket.class;
	}

	@Override
	public String getCacheName() {
		return TemperatureRecordDataCache.CACHE_NAME;
	}

	@Override
	public IdentificationCacheNode<TemperatureRecordDataCache> create(IdentifierFragment identifierFragment, List<TimestampedPacket<TemperaturePacket>> timestampedPackets, Instant periodStart, Duration periodDuration) {
		long periodStartDateMillis = periodStart.toEpochMilli();
		long previousPeriodStartDateMillis = periodStartDateMillis - periodDuration.toMillis();
		long unknownCutOffDateMillis = periodStartDateMillis - CacheHandler.INFO_DURATION.toMillis();
		long periodEndDateMillis = periodStart.toEpochMilli() + periodDuration.toMillis();


		boolean firstInPeriod = true;
		// initializations to -1 are necessary. We will use firstInPeriod to know if they have been initialized
		float min = -1;
		long minDateMillis = -1;
		float max = -1;
		long maxDateMillis = -1;

		MutableIntegral mainTemperatureCelsiusHourIntegral = new TrapezoidalRuleAccumulator();

		TimestampedPacket<TemperaturePacket> lastDataBeforePreviousPeriodStart = null;
		TimestampedPacket<TemperaturePacket> lastDataBeforePeriodStart = null;
		TimestampedPacket<TemperaturePacket> firstDataAfterPeriodStart = null;
		TimestampedPacket<TemperaturePacket> lastDataBeforePeriodEnd = null;

		for (TimestampedPacket<TemperaturePacket> packet : timestampedPackets) {
			final float temperatureCelsius = packet.getPacket().getTemperatureCelsius();
			final long dateMillis = packet.getDateMillis();
			if (dateMillis >= periodEndDateMillis) {
				break;
			}
			if (dateMillis < unknownCutOffDateMillis) {
				continue;
			}

			if (dateMillis < previousPeriodStartDateMillis) {
				lastDataBeforePreviousPeriodStart = packet;
			} else if (dateMillis < periodStartDateMillis) {
				lastDataBeforePeriodStart = packet;
			} else { // inside the current period
				if (firstDataAfterPeriodStart == null) {
					firstDataAfterPeriodStart = packet;
				}
				lastDataBeforePeriodEnd = packet;

				if (firstInPeriod || temperatureCelsius < min) {
					min = temperatureCelsius;
					minDateMillis = dateMillis;
				}
				if (firstInPeriod || temperatureCelsius > max) {
					max = temperatureCelsius;
					maxDateMillis = dateMillis;
				}
				if (firstInPeriod && lastDataBeforePeriodStart != null) {
					double hours = lastDataBeforePeriodStart.getDateMillis() / (1000.0 * 60 * 60);
					mainTemperatureCelsiusHourIntegral.add(hours, lastDataBeforePeriodStart.getPacket().getTemperatureCelsius());
				}
				double hours = dateMillis / (1000.0 * 60 * 60);
				mainTemperatureCelsiusHourIntegral.add(hours, temperatureCelsius);
				firstInPeriod = false;
			}

		}
		final TemperatureRecordDataCache.Record record;
		final Long firstDateMillis;
		final Long lastDateMillis;
		final Long unknownStartDateMillis;

		if (firstInPeriod) {
			record = null;
			firstDateMillis = null;
			lastDateMillis = null;
			unknownStartDateMillis = null;
		} else {
			final double unknownTemperatureCelsiusHours;
			final long unknownDurationMillis;
			if (lastDataBeforePeriodStart == null) { // we have no data from the previous period
				firstDateMillis = firstDataAfterPeriodStart.getDateMillis();
				// we will only have "unknown" data if there is no data in the previous period and we were able to get data from before the previous period
				if (lastDataBeforePreviousPeriodStart == null) {
					unknownStartDateMillis = null;
					unknownDurationMillis = 0;
					unknownTemperatureCelsiusHours = 0.0;
				} else {
					unknownStartDateMillis = lastDataBeforePreviousPeriodStart.getDateMillis();
					unknownDurationMillis = firstDataAfterPeriodStart.getDateMillis() - unknownStartDateMillis;
					double unknownPeriodAverageTemperatureCelsius = (lastDataBeforePreviousPeriodStart.getPacket().getTemperatureCelsius() + firstDataAfterPeriodStart.getPacket().getTemperatureCelsius()) / 2.0;
					double hours = unknownDurationMillis / (1000.0 * 60 * 60);
					unknownTemperatureCelsiusHours = unknownPeriodAverageTemperatureCelsius * hours;
				}
			} else {
				firstDateMillis = lastDataBeforePeriodStart.getDateMillis();
				unknownStartDateMillis = null;
				unknownDurationMillis = 0;
				unknownTemperatureCelsiusHours = 0.0;
			}
			lastDateMillis = lastDataBeforePeriodEnd.getDateMillis();
			record = new TemperatureRecordDataCache.Record(
					min, minDateMillis,
					max, maxDateMillis,
					unknownTemperatureCelsiusHours, unknownDurationMillis,
					mainTemperatureCelsiusHourIntegral.getIntegral(),
					lastDataBeforePeriodEnd.getDateMillis() - firstDateMillis,
					0.0, 0L
			);
		}
		return new IdentificationCacheNode<>(
				identifierFragment.getFragmentId(),
				new TemperatureRecordDataCache(
						identifierFragment.getIdentifier(),
						firstDateMillis, lastDateMillis, unknownStartDateMillis,
						record
				)
		);
	}
}
