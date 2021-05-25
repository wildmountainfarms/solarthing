package me.retrodaredevil.solarthing.solcast.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.Instant;

public final class EstimatedActual implements SimpleEstimatedActual {
	private final float pvEstimate;
	private final Instant periodEnd;
	private final Duration period;

	@JsonCreator
	public EstimatedActual(
			@JsonProperty(value = "pv_estimate", required = true) float pvEstimate,
			@JsonProperty(value = "period_end", required = true) Instant periodEnd,
			@JsonProperty(value = "period", required = true) Duration period) {
		this.pvEstimate = pvEstimate;
		this.periodEnd = periodEnd;
		this.period = period;
	}

	@Override
	public float getPVEstimate() {
		return pvEstimate;
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
