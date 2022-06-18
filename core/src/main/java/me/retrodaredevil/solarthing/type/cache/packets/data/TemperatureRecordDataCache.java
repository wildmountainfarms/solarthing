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
public class TemperatureRecordDataCache extends BaseAccumulationDataCache {
	public static final String CACHE_NAME = "temperatureRecord";

	private final Identifier identifier;
	private final Record record;

	@JsonCreator
	public TemperatureRecordDataCache(
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
	public @Nullable TemperatureRecordDataCache.Record getRecord() {
		return record;
	}

	@Override
	public TemperatureRecordDataCache combine(IdentificationCacheData other) {
		TemperatureRecordDataCache data = (TemperatureRecordDataCache) other;
		if (isUnknown()) {
			return data;
		}
		if (data.isUnknown()) {
			return this;
		}
		final float min;
		final long minDateMillis;
		if (record.minTemperatureCelsius <= data.record.minTemperatureCelsius) {
			min = record.minTemperatureCelsius;
			minDateMillis = record.minTemperatureCelsiusDateMillis;
		} else {
			min = data.record.minTemperatureCelsius;
			minDateMillis = data.record.minTemperatureCelsiusDateMillis;
		}
		final float max;
		final long maxDateMillis;
		if (record.maxTemperatureCelsius >= data.record.maxTemperatureCelsius) {
			max = record.maxTemperatureCelsius;
			maxDateMillis = record.maxTemperatureCelsiusDateMillis;
		} else {
			max = data.record.maxTemperatureCelsius;
			maxDateMillis = data.record.maxTemperatureCelsiusDateMillis;
		}
		return new TemperatureRecordDataCache(
				identifier,
				getFirstDateMillis(),
				data.getLastDateMillis(),
				getUnknownStartDateMillis(),
				new Record(
						min, minDateMillis,
						max, maxDateMillis,
						record.unknownTemperatureCelsiusHours, record.unknownDurationMillis,
						record.temperatureCelsiusHours + data.record.temperatureCelsiusHours, record.knownDurationMillis + data.record.knownDurationMillis,
						record.gapTemperatureCelsiusHours + data.record.gapTemperatureCelsiusHours + data.record.unknownTemperatureCelsiusHours,
						record.gapDurationMillis + data.record.gapDurationMillis + data.record.unknownDurationMillis
				)
		);
	}

	@JsonProperty("identifier")
	@Override
	public @NotNull Identifier getIdentifier() {
		return identifier;
	}

	@JsonExplicit
	public static class Record {

		private final float minTemperatureCelsius;
		private final long minTemperatureCelsiusDateMillis;
		private final float maxTemperatureCelsius;
		private final long maxTemperatureCelsiusDateMillis;

		private final double unknownTemperatureCelsiusHours;
		private final long unknownDurationMillis;

		private final double temperatureCelsiusHours;
		private final long knownDurationMillis;

		private final double gapTemperatureCelsiusHours;
		private final long gapDurationMillis;

		@JsonCreator
		public Record(
				@JsonProperty(value = "minTemperatureCelsius", required = true) float minTemperatureCelsius,
				@JsonProperty(value = "minTemperatureCelsiusDateMillis", required = true) long minTemperatureCelsiusDateMillis,
				@JsonProperty(value = "maxTemperatureCelsius", required = true) float maxTemperatureCelsius,
				@JsonProperty(value = "maxTemperatureCelsiusDateMillis", required = true) long maxTemperatureCelsiusDateMillis,
				@JsonProperty(value = "unknownTemperatureCelsiusHours", required = true) double unknownTemperatureCelsiusHours,
				@JsonProperty(value = "unknownDurationMillis", required = true) long unknownDurationMillis,
				@JsonProperty(value = "temperatureCelsiusHours", required = true) double temperatureCelsiusHours,
				@JsonProperty(value = "knownDurationMillis", required = true) long knownDurationMillis,
				@JsonProperty("gapTemperatureCelsiusHours") Double gapTemperatureCelsiusHours,
				@JsonProperty("gapDurationMillis") Long gapDurationMillis
		) {
			this.minTemperatureCelsius = minTemperatureCelsius;
			this.minTemperatureCelsiusDateMillis = minTemperatureCelsiusDateMillis;
			this.maxTemperatureCelsius = maxTemperatureCelsius;
			this.maxTemperatureCelsiusDateMillis = maxTemperatureCelsiusDateMillis;
			this.unknownTemperatureCelsiusHours = unknownTemperatureCelsiusHours;
			this.unknownDurationMillis = unknownDurationMillis;
			this.temperatureCelsiusHours = temperatureCelsiusHours;
			this.knownDurationMillis = knownDurationMillis;
			this.gapTemperatureCelsiusHours = gapTemperatureCelsiusHours == null ? 0.0 : gapTemperatureCelsiusHours;
			this.gapDurationMillis = gapDurationMillis == null ? 0L : gapDurationMillis;
		}

		@JsonProperty("minTemperatureCelsius")
		public float getMinTemperatureCelsius() { return minTemperatureCelsius; }
		@JsonProperty("minTemperatureCelsiusDateMillis")
		public long getMinTemperatureCelsiusDateMillis() { return minTemperatureCelsiusDateMillis; }

		@JsonProperty("maxTemperatureCelsius")
		public float getMaxTemperatureCelsius() { return maxTemperatureCelsius; }
		@JsonProperty("maxTemperatureCelsiusDateMillis")
		public long getMaxTemperatureCelsiusDateMillis() { return maxTemperatureCelsiusDateMillis; }

		@JsonProperty("unknownTemperatureCelsiusHours")
		public double getUnknownTemperatureCelsiusHours() { return unknownTemperatureCelsiusHours; }
		@JsonProperty("unknownDurationMillis")
		public long getUnknownDurationMillis() { return unknownDurationMillis; }

		@JsonProperty("temperatureCelsiusHours")
		public double getTemperatureCelsiusHours() { return temperatureCelsiusHours; }
		@JsonProperty("knownDurationMillis")
		public long getKnownDurationMillis() { return knownDurationMillis; }

		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		@JsonProperty("gapTemperatureCelsiusHours")
		public double getGapTemperatureCelsiusHours() { return gapTemperatureCelsiusHours; }
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		@JsonProperty("gapDurationMillis")
		public long getGapDurationMillis() { return gapDurationMillis; }
	}
}
