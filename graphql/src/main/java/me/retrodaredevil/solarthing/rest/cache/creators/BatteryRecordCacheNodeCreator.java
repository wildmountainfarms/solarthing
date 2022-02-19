package me.retrodaredevil.solarthing.rest.cache.creators;

import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.rest.cache.CacheHandler;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.type.cache.packets.data.BatteryRecordDataCache;
import me.retrodaredevil.solarthing.util.integration.MutableIntegral;
import me.retrodaredevil.solarthing.util.integration.TrapezoidalRuleAccumulator;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class BatteryRecordCacheNodeCreator implements IdentificationCacheNodeCreator<BatteryRecordDataCache, BatteryVoltage> {
	@Override
	public Class<BatteryVoltage> getAcceptedType() {
		return BatteryVoltage.class;
	}

	@Override
	public String getCacheName() {
		return BatteryRecordDataCache.CACHE_NAME;
	}

	@Override
	public IdentificationCacheNode<BatteryRecordDataCache> create(IdentifierFragment identifierFragment, List<TimestampedPacket<BatteryVoltage>> timestampedPackets, Instant periodStart, Duration periodDuration) {
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

		MutableIntegral mainVoltHourIntegral = new TrapezoidalRuleAccumulator();

		TimestampedPacket<BatteryVoltage> lastDataBeforePreviousPeriodStart = null;
		TimestampedPacket<BatteryVoltage> lastDataBeforePeriodStart = null;
		TimestampedPacket<BatteryVoltage> firstDataAfterPeriodStart = null;
		TimestampedPacket<BatteryVoltage> lastDataBeforePeriodEnd = null;

		for (TimestampedPacket<BatteryVoltage> packet : timestampedPackets) {
			final float voltage = packet.getPacket().getBatteryVoltage();
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
			} else {
				if (firstDataAfterPeriodStart == null) {
					firstDataAfterPeriodStart = packet;
				}
				lastDataBeforePeriodEnd = packet;

				if (firstInPeriod || voltage < min) {
					min = voltage;
					minDateMillis = dateMillis;
				}
				if (firstInPeriod || voltage > max) {
					max = voltage;
					maxDateMillis = dateMillis;
				}
				if (firstInPeriod && lastDataBeforePeriodStart != null) {
					double hours = lastDataBeforePeriodStart.getDateMillis() / (1000.0 * 60 * 60);
					mainVoltHourIntegral.add(hours, lastDataBeforePeriodStart.getPacket().getBatteryVoltage());
				}
				double hours = dateMillis / (1000.0 * 60 * 60);
				mainVoltHourIntegral.add(hours, voltage);
				firstInPeriod = false;
			}

		}
		final BatteryRecordDataCache.Record record;
		final Long firstDateMillis;
		final Long lastDateMillis;
		final Long unknownStartDateMillis;

		if (firstInPeriod) {
			record = null;
			firstDateMillis = null;
			lastDateMillis = null;
			unknownStartDateMillis = null;
		} else {
			final double unknownBatteryVoltageHours;
			final long unknownDurationMillis;
			if (lastDataBeforePeriodStart == null) { // we have no data from the previous period
				firstDateMillis = firstDataAfterPeriodStart.getDateMillis();
				// we will only have "unknown" data if there is no data in the previous period and we were able to get data from before the previous period
				if (lastDataBeforePreviousPeriodStart == null) {
					unknownStartDateMillis = null;
					unknownDurationMillis = 0;
					unknownBatteryVoltageHours = 0.0;
				} else {
					unknownStartDateMillis = lastDataBeforePreviousPeriodStart.getDateMillis();
					unknownDurationMillis = firstDataAfterPeriodStart.getDateMillis() - unknownStartDateMillis;
					double unknownPeriodAverageVoltage = (lastDataBeforePreviousPeriodStart.getPacket().getBatteryVoltage() + firstDataAfterPeriodStart.getPacket().getBatteryVoltage()) / 2.0;
					double hours = unknownDurationMillis / (1000.0 * 60 * 60);
					unknownBatteryVoltageHours = unknownPeriodAverageVoltage * hours;
				}
			} else {
				firstDateMillis = lastDataBeforePeriodStart.getDateMillis();
				unknownStartDateMillis = null;
				unknownDurationMillis = 0;
				unknownBatteryVoltageHours = 0.0;
			}
			lastDateMillis = lastDataBeforePeriodEnd.getDateMillis();
			record = new BatteryRecordDataCache.Record(
					min, minDateMillis,
					max, maxDateMillis,
					unknownBatteryVoltageHours, unknownDurationMillis,
					mainVoltHourIntegral.getIntegral(),
					lastDataBeforePeriodEnd.getDateMillis() - firstDateMillis,
					0.0, 0
			);
		}
		return new IdentificationCacheNode<>(
				identifierFragment.getFragmentId(),
				new BatteryRecordDataCache(
						identifierFragment.getIdentifier(),
						firstDateMillis, lastDateMillis, unknownStartDateMillis,
						record
				)
		);
	}
}
