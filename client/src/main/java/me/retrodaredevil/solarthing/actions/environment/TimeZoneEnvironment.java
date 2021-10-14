package me.retrodaredevil.action.node.environment;

import java.time.ZoneId;
import java.util.TimeZone;

public class TimeZoneEnvironment {
	private final TimeZone timeZone;

	public TimeZoneEnvironment(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}
	public ZoneId getZoneId() {
		return timeZone.toZoneId();
	}
}
