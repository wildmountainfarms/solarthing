package me.retrodaredevil.solarthing.config.options;

import org.jspecify.annotations.NullMarked;

import java.time.ZoneId;

@NullMarked
public interface TimeZoneOption extends ProgramOptions {
	ZoneId getZoneId();
}
