package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.time.Duration;
import java.time.LocalTime;
import java.time.MonthDay;

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
	 * Used for 0x903E, 0x903F, 0x9065
	 * @param duration
	 * @return
	 */
	public static int convertDurationToTracerDurationRaw(Duration duration) {
		long totalMinutes = duration.toMinutes();
		long hours = totalMinutes / 60;
		long minutes = totalMinutes % 60;
		return ((int) minutes) | ((int) hours << 8);
	}

	/**
	 * @param secondMinuteHourRaw 48 bit number where low 16 bits are seconds, middle 16 bits are minutes and high 16 bits is hour
	 */
	public static LocalTime convertTracer48BitRawTimeToLocalTime(long secondMinuteHourRaw) {
		return LocalTime.of(
				(int) ((secondMinuteHourRaw >> 32L) & 0xFF),
				(int) ((secondMinuteHourRaw >> 16L) & 0xFF),
				(int) (secondMinuteHourRaw & 0xFF)
		);
	}
	public static long convertLocalTimeToTracer48BitRawTime(LocalTime time) {
		return ((long) time.getHour() << 32L) | ((long) time.getMinute() << 16L) | time.getSecond();
	}

	public static LocalTime extractTracer48BitRawInstantToLocalTime(long raw) {
		int seconds = (int) (raw & 0xFF);
		int minutes = (int) ((raw >> 8) & 0xFF);
		int hours = (int) ((raw >> 16) & 0xFF);
		return LocalTime.of(hours, minutes, seconds);
	}
	public static MonthDay extractTracer48BitRawInstantToMonthDay(long raw) {
		int day = (int) ((raw >> 24) & 0xFF);
		int month = (int) ((raw >> 32) & 0xFF);
		return MonthDay.of(month, day);
	}
	public static int extractTracer48BitRawInstantToYearNumber(long raw) {
		return (int) ((raw >> 40) & 0xFF);
	}
	public static long convertInstantToTracer48BitRaw(int yearNumber, MonthDay monthDay, LocalTime time) {
		return ((long) yearNumber << 40L) | ((long) monthDay.getMonthValue() << 32L) | ((long) monthDay.getDayOfMonth() << 24L) |
				(time.getHour() << 16) | (time.getMinute() << 8) | time.getSecond();
	}
}
