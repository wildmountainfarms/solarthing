package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static java.util.Objects.requireNonNull;

@NullMarked
public class TracerClockOptions {
	private final Duration durationThreshold;
	private final @Nullable ZoneOffset zoneOffset;
	private final @Nullable ZoneId zoneId;

	@JsonCreator
	public TracerClockOptions(
			@JsonProperty(value = "threshold", required = true) Duration durationThreshold,
			@JsonProperty("offset") @Nullable ZoneOffset zoneOffset,
			@JsonProperty("zone") @Nullable ZoneId zoneId) {
		this.durationThreshold = requireNonNull(durationThreshold);
		this.zoneOffset = zoneOffset;
		this.zoneId = zoneId;

		if (durationThreshold.isNegative()) {
			throw new IllegalArgumentException("durationThreshold cannot be negative!");
		}
		if (zoneOffset != null && zoneId != null) {
			throw new IllegalArgumentException("You cannot supply a zoneOffset and a zoneId!");
		}
	}

	public Duration getDurationThreshold() {
		return durationThreshold;
	}

	public @Nullable ZoneOffset getZoneOffset() {
		return zoneOffset;
	}

	public @Nullable ZoneId getZoneId() {
		return zoneId;
	}
}
