package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.solar.SolarStatusPacket;

import java.time.LocalDateTime;

public interface TracerStatusPacket extends TracerReadTable, SolarStatusPacket {

	default LocalDateTime getDate() {
		int secondsMinutesRaw = getSecondsMinutesRaw();
		int hourDayRaw = getHourDayRaw();
		int monthYearRaw = getMonthYearRaw();

		return TracerUtil.convertTracerRawToDateTime(secondsMinutesRaw, hourDayRaw, monthYearRaw);
	}
}
