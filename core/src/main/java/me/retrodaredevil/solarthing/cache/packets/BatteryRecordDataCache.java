package me.retrodaredevil.solarthing.cache.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.cache.IdentificationCacheData;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public class BatteryRecordDataCache implements IdentificationCacheData {
	private final Identifier identifier;
	private final Float minBatteryVoltage;
	private final Long minBatteryVoltageDateMillis;
	private final Float maxBatteryVoltage;
	private final Long maxBatteryVoltageDateMillis;

	@JsonCreator
	public BatteryRecordDataCache(
			@JsonProperty(value = "identifier", required = true) Identifier identifier,
			@JsonProperty(value = "minBatteryVoltage", required = true) Float minBatteryVoltage,
			@JsonProperty(value = "minBatteryVoltageDateMillis", required = true) Long minBatteryVoltageDateMillis,
			@JsonProperty(value = "maxBatteryVoltage", required = true) Float maxBatteryVoltage,
			@JsonProperty(value = "maxBatteryVoltageDateMillis", required = true) Long maxBatteryVoltageDateMillis) {
		requireNonNull(this.identifier = identifier);
		this.minBatteryVoltage = minBatteryVoltage;
		this.minBatteryVoltageDateMillis = minBatteryVoltageDateMillis;
		this.maxBatteryVoltage = maxBatteryVoltage;
		this.maxBatteryVoltageDateMillis = maxBatteryVoltageDateMillis;

		if (minBatteryVoltage == null) {
			if (minBatteryVoltageDateMillis != null || maxBatteryVoltage != null || maxBatteryVoltageDateMillis != null) {
				throw new IllegalStateException("If one thing is null, everything must be null!");
			}
		} else {
			if (minBatteryVoltageDateMillis == null || maxBatteryVoltage == null || maxBatteryVoltageDateMillis == null) {
				throw new IllegalStateException("If one thing is non-null, everything must be non-null!");
			}
		}
	}
	public boolean isUnknown() {
		return minBatteryVoltageDateMillis == null;
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
		if (minBatteryVoltage <= data.minBatteryVoltage) {
			min = minBatteryVoltage;
			minDateMillis = minBatteryVoltageDateMillis;
		} else {
			min = data.minBatteryVoltage;
			minDateMillis = data.minBatteryVoltageDateMillis;
		}
		final float max;
		final long maxDateMillis;
		if (maxBatteryVoltage >= data.maxBatteryVoltage) {
			max = maxBatteryVoltage;
			maxDateMillis = maxBatteryVoltageDateMillis;
		} else {
			max = data.maxBatteryVoltage;
			maxDateMillis = data.maxBatteryVoltageDateMillis;
		}
		return new BatteryRecordDataCache(
				identifier,
				min, minDateMillis,
				max, maxDateMillis
		);
	}

	@JsonProperty("identifier")
	@Override
	public @NotNull Identifier getIdentifier() {
		return identifier;
	}

	@JsonProperty("minBatteryVoltage")
	public Float getMinBatteryVoltage() {
		return minBatteryVoltage;
	}

	@JsonProperty("minBatteryVoltageDateMillis")
	public Long getMinBatteryVoltageDateMillis() {
		return minBatteryVoltageDateMillis;
	}

	@JsonProperty("maxBatteryVoltage")
	public Float getMaxBatteryVoltage() {
		return maxBatteryVoltage;
	}

	@JsonProperty("maxBatteryVoltageDateMillis")
	public Long getMaxBatteryVoltageDateMillis() {
		return maxBatteryVoltageDateMillis;
	}
}
