package me.retrodaredevil.solarthing.solcast.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

public interface SimpleEstimatedActual extends IntervalData {
	@JsonProperty("pv_estimate")
	float getPVEstimate();

	@GraphQLInclude("pv_estimate_watts")
	default float getPVEstimateWatts() {
		return getPVEstimate() * 1000.0f;
	}

	/**
	 *
	 * @return The energy generation in KWH over this period of time
	 */
	default float getEnergyGenerationEstimate() {
		return getPVEstimate() * getPeriod().toMinutes() / 60.0f;
	}
}
