package me.retrodaredevil.solarthing.solcast.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;

public interface IntervalData {
	@JsonProperty("period_end")
	Instant getPeriodEnd();
	@JsonProperty("period")
	Duration getPeriod();

	Comparator<IntervalData> END_PERIOD_COMPARATOR = Comparator.comparing(IntervalData::getPeriodEnd);
}
