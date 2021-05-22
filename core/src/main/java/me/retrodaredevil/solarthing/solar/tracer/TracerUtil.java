package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@UtilityClass
public final class TracerUtil {
	private TracerUtil() { throw new UnsupportedOperationException(); }

	/**
	 * @param minuteHourRaw 16 bit number where low 8 bits is minute and high 8 bits is hour
	 */
	public static Duration convertTracerDurationRawToDuration(int minuteHourRaw) {
		int minutes = minuteHourRaw & 0xFF;
		int hours = minuteHourRaw >> 8;
		return Duration.ofMinutes(minutes + hours * 60);
	}

	/**
	 * @param secondMinuteHourRaw 24 bit number where high 8 bits are seconds, middle 8 bits are minutes and low 8 bits is hour
	 */
	public static LocalTime convertTracerRawTimeToLocalTime(int secondMinuteHourRaw) {
		return LocalTime.of(
				secondMinuteHourRaw & 0xFF,
				(secondMinuteHourRaw >> 8) & 0xFF,
				secondMinuteHourRaw >> 16
		);
	}

	/**
	 * @param secondsMinutesRaw 16 bit number where low 8 bits are seconds and high 8 bits are minutes
	 * @param hourDayRaw 16 bit number where low 8 bits is hour and high 8 bits is day
	 * @param monthYearRaw 16 bit number where low 8 bits is month and high 8 bits is year
	 */
	public static LocalDateTime convertTracerRawToDateTime(int secondsMinutesRaw, int hourDayRaw, int monthYearRaw) {
		int seconds = secondsMinutesRaw & 0xFF;
		int minutes = secondsMinutesRaw >> 8;
		int hours = hourDayRaw & 0xFF;
		int day = hourDayRaw >> 8;
		int month = monthYearRaw & 0xFF;
		int year = monthYearRaw >> 8;

		return LocalDateTime.of(year, month, day, hours, minutes, seconds);
	}
}
