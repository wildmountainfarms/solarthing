package me.retrodaredevil.solarthing.util;

public final class TimeRange {
	public static final TimeRange ALWAYS = new TimeRange(null, null);

	private final Long startTime;
	private final Long endTime;

	public TimeRange(Long startTime, Long endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		if (startTime != null && endTime != null) {
			if (startTime > endTime) {
				throw new IllegalArgumentException("Start time cannot be greater than end time!");
			}
		}
	}
	public static TimeRange create(long startTime, long endTime) {
		return new TimeRange(startTime, endTime);
	}
	public static TimeRange createAfter(long startTime) {
		return new TimeRange(startTime, null);
	}
	public static TimeRange createBefore(long endTime) {
		return new TimeRange(null, endTime);
	}

	public Long getStartTime() {
		return startTime;
	}

	public Long getEndTime() {
		return endTime;
	}
	public boolean isForever() {
		return startTime == null && endTime == null;
	}
	public boolean contains(long timeMillis) {
		return (startTime == null || timeMillis >= startTime) && (endTime == null || timeMillis < endTime);
	}

	public boolean fullyContains(TimeRange timeRange) {
		if (startTime == null && endTime == null) {
			return true;
		}
		if (startTime != null && timeRange.startTime == null) {
			return false;
		}
		if (endTime != null && timeRange.endTime == null) {
			return false;
		}
		if (startTime == null) {
			return endTime >= timeRange.endTime;
		}
		if (endTime == null) {
			return startTime <= timeRange.startTime;
		}
		return startTime <= timeRange.startTime && endTime >= timeRange.endTime;
	}
	private boolean doIntersects(TimeRange timeRange) {
		return isForever()
				|| (startTime == null && timeRange.startTime == null)
				|| (endTime == null && timeRange.endTime == null)
				|| (timeRange.startTime != null && contains(timeRange.startTime))
				|| (timeRange.endTime != null && contains(timeRange.endTime))
				|| fullyContains(timeRange);
	}
	public boolean intersects(TimeRange timeRange) {
		return doIntersects(timeRange) || timeRange.doIntersects(this);
	}
}
