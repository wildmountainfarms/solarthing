package me.retrodaredevil.solarthing.solcast.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

import java.time.Duration;
import java.time.Instant;

public final class Forecast implements SimpleEstimatedActual {
	private final float pvEstimate;
	private final float pvEstimate10;
	private final float pvEstimate90;
	private final Instant periodEnd;
	private final Duration period;

	@JsonCreator
	public Forecast(
			@JsonProperty("pv_estimate") float pvEstimate,
			@JsonProperty("pv_estimate10") float pvEstimate10, @JsonProperty("pv_estimate90") float pvEstimate90,
			@JsonProperty("period_end") Instant periodEnd, @JsonProperty("period") Duration period) {
		this.pvEstimate = pvEstimate;
		this.pvEstimate10 = pvEstimate10;
		this.pvEstimate90 = pvEstimate90;
		this.periodEnd = periodEnd;
		this.period = period;
	}

	@Override
	public float getPVEstimate() {
		return pvEstimate;
	}

	@JsonProperty("pv_estimate10")
	public float getPVEstimate10() {
		return pvEstimate10;
	}
	@GraphQLInclude("pv_estimate10_watts")
	public float getPVEstimate10Watts() {
		return getPVEstimate10() * 1000.0f;
	}

	@JsonProperty("pv_estimate90")
	public float getPVEstimate90() {
		return pvEstimate90;
	}
	@GraphQLInclude("pv_estimate90_watts")
	public float getPVEstimate90Watts() {
		return getPVEstimate90() * 1000.0f;
	}

	@Override
	public Instant getPeriodEnd() {
		return periodEnd;
	}

	@Override
	public Duration getPeriod() {
		return period;
	}
}
