package me.retrodaredevil.solarthing.solcast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.Instant;

public final class Measurement {
	private final Instant periodEnd;
	private final Duration period;
	private final float totalPowerKilowatts;

	@JsonCreator
	public Measurement(@JsonProperty("period_end") Instant periodEnd, @JsonProperty("period") Duration period, @JsonProperty("total_power") float totalPowerKilowatts) {
		this.periodEnd = periodEnd;
		this.period = period;
		this.totalPowerKilowatts = totalPowerKilowatts;
	}

	@JsonProperty("period_end")
	public Instant getPeriodEnd() {
		return periodEnd;
	}

	@JsonProperty("period")
	public Duration getPeriod() {
		return period;
	}

	@JsonProperty("total_power")
	public float getTotalPowerKilowatts() {
		return totalPowerKilowatts;
	}
}
