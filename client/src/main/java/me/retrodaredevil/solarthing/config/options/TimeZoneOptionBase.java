package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.TimeZone;

public abstract class TimeZoneOptionBase implements TimeZoneOption {
	@JsonProperty("time_zone")
	private TimeZone timeZone = null;
	@Override
	public TimeZone getTimeZone(){
		TimeZone r = this.timeZone;
		if(r == null){
			return TimeZone.getDefault();
		}
		return r;
	}
}
