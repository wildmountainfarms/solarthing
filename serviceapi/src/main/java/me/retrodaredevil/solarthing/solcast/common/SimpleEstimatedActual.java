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
}
