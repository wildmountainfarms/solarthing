package me.retrodaredevil.solarthing.actions.environment;

import java.time.ZoneId;

public class TimeZoneEnvironment {
	private final ZoneId zoneId;

	public TimeZoneEnvironment(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}
}
