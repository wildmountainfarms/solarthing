package me.retrodaredevil.solarthing.config.options;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.time.ZoneId;

public interface TimeZoneOption extends ProgramOptions {
	@NotNull ZoneId getZoneId();
}
