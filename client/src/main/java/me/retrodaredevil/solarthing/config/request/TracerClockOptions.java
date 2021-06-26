package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static java.util.Objects.requireNonNull;

public class TracerClockOptions {
	private final Duration durationThreshold;
	private final ZoneOffset zoneOffset;
	private final ZoneId zoneId;

	@JsonCreator
	public TracerClockOptions(
			@JsonProperty("threshold") Duration durationThreshold,
			@JsonProperty("offset") ZoneOffset zoneOffset,
			@JsonProperty("zone") ZoneId zoneId) {
		requireNonNull(this.durationThreshold = durationThreshold);
		this.zoneOffset = zoneOffset;
		this.zoneId = zoneId;

		if (durationThreshold.isNegative()) {
			throw new IllegalArgumentException("durationThreshold cannot be negative!");
		}
		if (zoneOffset != null && zoneId != null) {
			throw new IllegalArgumentException("You cannot supply a zoneOffset and a zoneId!");
		}
	}

	public @NotNull Duration getDurationThreshold() {
		return durationThreshold;
	}

	public ZoneOffset getZoneOffset() {
		return zoneOffset;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}
}
