package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.time.ZoneId;

abstract class TimeZoneOptionBase implements TimeZoneOption {
	@JsonProperty("time_zone")
	private @Nullable ZoneId zoneId = null;

	@Override
	public @NotNull ZoneId getZoneId() {
		ZoneId r = zoneId;
		if (r == null) {
			return ZoneId.systemDefault();
		}
		return r;
	}
}
