package me.retrodaredevil.solarthing.type.cache.packets.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public class BatteryRecordDataCache extends BaseAccumulationDataCache {
	public static final String CACHE_NAME = "batteryRecord";

	private final Identifier identifier;
	private final Record record;

	@JsonCreator
	public BatteryRecordDataCache(
			@JsonProperty(value = "identifier", required = true) Identifier identifier,
			@JsonProperty(value = "firstDateMillis", required = true) Long firstDateMillis,
			@JsonProperty(value = "lastDateMillis", required = true) Long lastDateMillis,
			@JsonProperty(value = "unknownStartDateMillis", required = true) Long unknownStartDateMillis,
			@JsonProperty(value = "record", required = true) Record record) {
		super(firstDateMillis, lastDateMillis, unknownStartDateMillis);
		requireNonNull(this.identifier = identifier);
		this.record = record;

		if ((firstDateMillis == null) != (record == null)) {
			throw new IllegalArgumentException("Both firstDateMillis and record must be null or non-null at the same time!");
		}
		if (record != null) {
			if (record.unknownDurationMillis == 0L && unknownStartDateMillis != null) {
				throw new IllegalArgumentException("If unknownStartDuration is 0, then unknownStartDateMillis must be null! unknownStartDateMillis: " + unknownStartDateMillis);
			}
			if (record.unknownDurationMillis != 0L && unknownStartDateMillis == null) {
				throw new IllegalArgumentException("If unknownDurationMillis is not 0, then unknownStartDateMillis should not be null! unknownDurationMillis: " + record.unknownDurationMillis);
			}
		}
	}

	@JsonProperty("record")
	public @Nullable BatteryRecordDataCache.Record getRecord() {
		return record;
	}

	@Override
	public BatteryRecordDataCache combine(IdentificationCacheData other) {
		BatteryRecordDataCache data = (BatteryRecordDataCache) other;
		if (isUnknown()) {
			return data;
		}
		if (data.isUnknown()) {
			return this;
		}
		final float min;
		final long minDateMillis;
		if (record.minBatteryVoltage <= data.record.minBatteryVoltage) {
			min = record.minBatteryVoltage;
			minDateMillis = record.minBatteryVoltageDateMillis;
		} else {
			min = data.record.minBatteryVoltage;
			minDateMillis = data.record.minBatteryVoltageDateMillis;
		}
		final float max;
		final long maxDateMillis;
		if (record.maxBatteryVoltage >= data.record.maxBatteryVoltage) {
			max = record.maxBatteryVoltage;
			maxDateMillis = record.maxBatteryVoltageDateMillis;
		} else {
			max = data.record.maxBatteryVoltage;
			maxDateMillis = data.record.maxBatteryVoltageDateMillis;
		}
		return new BatteryRecordDataCache(
				identifier,
				getFirstDateMillis(),
				data.getLastDateMillis(),
				getUnknownStartDateMillis(),
				new Record(
						min, minDateMillis,
						max, maxDateMillis,
						record.unknownBatteryVoltageHours, record.unknownDurationMillis,
						record.batteryVoltageHours + data.record.batteryVoltageHours, record.knownDurationMillis + data.record.knownDurationMillis,
						record.gapBatteryVoltageHours + data.record.gapBatteryVoltageHours + data.record.unknownBatteryVoltageHours,
						record.gapDurationMillis + data.record.gapDurationMillis + data.record.unknownDurationMillis
				)
		);
	}

	@JsonProperty("identifier")
	@Override
	public @NotNull Identifier getIdentifier() {
		return identifier;
	}

	@SuppressWarnings("JavaLangClash")
	@JsonExplicit
	public static class Record {

		private final float minBatteryVoltage;
		private final long minBatteryVoltageDateMillis;
		private final float maxBatteryVoltage;
		private final long maxBatteryVoltageDateMillis;

		/*
		Unknown and gap battery voltage hours are separated from batteryVoltageHours for a couple of reasons. First, unknown data is separated
		because unknown data may not have even been from the given period. Second, unknown data is not very precise. That
		voltage hour calculation had limited data to estimate the value, so its value and accuracy should be taken with a grain of salt.

		Now, when combining two pieces of data together, usually the two main values would be combined with the second's unknown data by addition.
		However, since unknown data is so imprecise, we decided to accumulate "gap" data in its own field. So the gap value is an accumulation
		of unknown data that fell in the middle of combined data.

		It is up to the user of the data to decide if they even want to use unknown or gap data.
		Note that gap data is only present for data that has been combined, so it should be 0 when stored in the database or not stored at all in the database.
		 */

		/** The battery voltage hours leading into known data */
		private final double unknownBatteryVoltageHours;
		private final long unknownDurationMillis;

		private final double batteryVoltageHours;
		private final long knownDurationMillis;

		private final double gapBatteryVoltageHours;
		private final long gapDurationMillis;

		@JsonCreator
		public Record(
				@JsonProperty(value = "minBatteryVoltage", required = true) float minBatteryVoltage,
				@JsonProperty(value = "minBatteryVoltageDateMillis", required = true) long minBatteryVoltageDateMillis,
				@JsonProperty(value = "maxBatteryVoltage", required = true) float maxBatteryVoltage,
				@JsonProperty(value = "maxBatteryVoltageDateMillis", required = true) long maxBatteryVoltageDateMillis,
				@JsonProperty(value = "unknownBatteryVoltageHours", required = true) double unknownBatteryVoltageHours,
				@JsonProperty(value = "unknownDurationMillis", required = true) long unknownDurationMillis,
				@JsonProperty(value = "batteryVoltageHours", required = true) double batteryVoltageHours,
				@JsonProperty(value = "knownDurationMillis", required = true) long knownDurationMillis,
				@JsonProperty("gapBatteryVoltageHours") Double gapBatteryVoltageHours,
				@JsonProperty("gapDurationMillis") Long gapDurationMillis
		) {
			this.minBatteryVoltage = minBatteryVoltage;
			this.minBatteryVoltageDateMillis = minBatteryVoltageDateMillis;
			this.maxBatteryVoltage = maxBatteryVoltage;
			this.maxBatteryVoltageDateMillis = maxBatteryVoltageDateMillis;
			this.unknownBatteryVoltageHours = unknownBatteryVoltageHours;
			this.unknownDurationMillis = unknownDurationMillis;
			this.batteryVoltageHours = batteryVoltageHours;
			this.knownDurationMillis = knownDurationMillis;
			this.gapBatteryVoltageHours = gapBatteryVoltageHours == null ? 0.0 : gapBatteryVoltageHours;
			this.gapDurationMillis = gapDurationMillis == null ? 0L : gapDurationMillis;
		}

		@JsonProperty("minBatteryVoltage")
		public float getMinBatteryVoltage() { return minBatteryVoltage; }
		@JsonProperty("minBatteryVoltageDateMillis")
		public long getMinBatteryVoltageDateMillis() { return minBatteryVoltageDateMillis; }

		@JsonProperty("maxBatteryVoltage")
		public float getMaxBatteryVoltage() { return maxBatteryVoltage; }
		@JsonProperty("maxBatteryVoltageDateMillis")
		public long getMaxBatteryVoltageDateMillis() { return maxBatteryVoltageDateMillis; }

		@JsonProperty("unknownBatteryVoltageHours")
		public double getUnknownBatteryVoltageHours() { return unknownBatteryVoltageHours; }
		@JsonProperty("unknownDurationMillis")
		public long getUnknownDurationMillis() { return unknownDurationMillis; }

		@JsonProperty("batteryVoltageHours")
		public double getBatteryVoltageHours() { return batteryVoltageHours; }
		@JsonProperty("knownDurationMillis")
		public long getKnownDurationMillis() { return knownDurationMillis; }

		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		@JsonProperty("gapBatteryVoltageHours")
		public double getGapBatteryVoltageHours() { return gapBatteryVoltageHours; }
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		@JsonProperty("gapDurationMillis")
		public long getGapDurationMillis() { return gapDurationMillis; }
	}
}
