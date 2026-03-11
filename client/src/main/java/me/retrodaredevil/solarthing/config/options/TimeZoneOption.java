package me.retrodaredevil.solarthing.config.options;

import org.jspecify.annotations.NonNull;

import java.time.ZoneId;

public interface TimeZoneOption extends ProgramOptions {
	@NonNull ZoneId getZoneId();
}
