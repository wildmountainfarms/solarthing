package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.ZoneId;

@NullMarked
abstract class TimeZoneOptionBase implements TimeZoneOption {
	@JsonProperty("time_zone")
	private @Nullable ZoneId zoneId = null;

	@Override
	public ZoneId getZoneId() {
		ZoneId r = zoneId;
		if (r == null) {
			return ZoneId.systemDefault();
		}
		return r;
	}
}
