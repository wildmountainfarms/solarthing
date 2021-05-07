package me.retrodaredevil.solarthing.actions.environment;

import java.util.TimeZone;

public class TimeZoneEnvironment {
	private final TimeZone timeZone;

	public TimeZoneEnvironment(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}
}
