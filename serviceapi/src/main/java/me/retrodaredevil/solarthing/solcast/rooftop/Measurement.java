package me.retrodaredevil.solarthing.solcast.rooftop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.solcast.common.IntervalData;

import java.time.Duration;
import java.time.Instant;

public final class Measurement implements IntervalData {
	private static final Duration MINIMUM_PERIOD = Duration.ofMinutes(5);
	private final Instant periodEnd;
	private final Duration period;
	private final float totalPowerKilowatts;

	@JsonCreator
	public Measurement(@JsonProperty("period_end") Instant periodEnd, @JsonProperty("period") Duration period, @JsonProperty("total_power") float totalPowerKilowatts) {
		this.periodEnd = periodEnd;
		this.period = period;
		this.totalPowerKilowatts = totalPowerKilowatts;

		if (period.compareTo(MINIMUM_PERIOD) < 0) {
			throw new IllegalArgumentException("period cannot be less than 5 minutes!");
		}
		if (totalPowerKilowatts < 0) {
			throw new IllegalArgumentException("totalPowerKilowatts cannot be negative!");
		}
	}

	@Override
	public Instant getPeriodEnd() {
		return periodEnd;
	}

	@Override
	public Duration getPeriod() {
		return period;
	}

	@JsonProperty("total_power")
	public float getTotalPowerKilowatts() {
		return totalPowerKilowatts;
	}
}
