package me.retrodaredevil.solarthing.config.options;

import java.time.ZoneId;
import java.util.TimeZone;

public interface TimeZoneOption extends ProgramOptions {
	TimeZone getTimeZone(); // TODO change to just zone id

	default ZoneId getZoneId() {
		return getTimeZone().toZoneId();
	}
}
