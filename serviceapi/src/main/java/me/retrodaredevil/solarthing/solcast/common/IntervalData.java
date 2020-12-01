package me.retrodaredevil.solarthing.solcast.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;

public interface IntervalData {
	@JsonProperty("period_end")
	Instant getPeriodEnd();
	@JsonProperty("period")
	Duration getPeriod();

	@GraphQLInclude("period_start")
	default Instant getPeriodStart() {
		return getPeriodEnd().minus(getPeriod());
	}
	@GraphQLInclude("period_midpoint")
	default Instant getPeriodMidpoint() {
		return getPeriodEnd().minus(getPeriod().dividedBy(2));
	}

	Comparator<IntervalData> END_PERIOD_COMPARATOR = Comparator.comparing(IntervalData::getPeriodEnd);
}
