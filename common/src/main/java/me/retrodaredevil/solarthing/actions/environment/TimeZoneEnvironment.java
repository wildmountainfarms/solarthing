package me.retrodaredevil.solarthing.actions.environment;

import java.time.ZoneId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TimeZoneEnvironment {
	private final ZoneId zoneId;

	public TimeZoneEnvironment(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}
}
