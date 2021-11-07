package me.retrodaredevil.solarthing.type.alter.flag;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.util.TimeRange;

import java.time.Instant;
import java.util.Objects;

@JsonExplicit
@JsonTypeName("TIME_RANGE")
public class TimeRangeActivePeriod implements ActivePeriod {
	private final TimeRange timeRange;

	@JsonCreator
	public TimeRangeActivePeriod(
			@JsonProperty(value = "startDateMillis", required = true) Long startDateMillis,
			@JsonProperty(value = "endDateMillis", required = true) Long endDateMillis
	) {
		this(new TimeRange(startDateMillis, endDateMillis));
	}
	public TimeRangeActivePeriod(TimeRange timeRange) {
		this.timeRange = timeRange;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}

	@JsonProperty("startDateMillis")
	private Long getStartDateMillis() {
		return timeRange.getStartTimeMillis();
	}
	@JsonProperty("endDateMillis")
	private Long getEndDateMillis() {
		return timeRange.getEndTimeMillis();
	}

	@Override
	public @NotNull ActivePeriodType getPacketType() {
		return ActivePeriodType.TIME_RANGE;
	}

	@Override
	public boolean isActive(long dateMillis) {
		return timeRange.contains(dateMillis);
	}

	@Override
	public boolean isActive(Instant instant) {
		return timeRange.contains(instant);
	}

	@Override
	public boolean encapsulatesAllOf(ActivePeriod activePeriod) {
		if (activePeriod instanceof TimeRangeActivePeriod) {
			TimeRange other = ((TimeRangeActivePeriod) activePeriod).timeRange;
			return timeRange.fullyContains(other);
		}
		return false;
	}

	@Override
	public @NotNull String getUniqueString() {
		return "TimeRangeActivePeriod(" +
				"timeRange=" + timeRange +
				')';
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TimeRangeActivePeriod that = (TimeRangeActivePeriod) o;
		return timeRange.equals(that.timeRange);
	}

	@Override
	public int hashCode() {
		return Objects.hash(timeRange);
	}
}
