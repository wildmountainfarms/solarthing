package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.text.DecimalFormat;
import java.text.Format;

@UtilityClass
public class TimeUtil {
	private TimeUtil() { throw new UnsupportedOperationException(); }

	private static final Format SECONDS_FORMAT = new DecimalFormat("0.000");

	public static String nanosToSecondsString(long nanos) {
		return SECONDS_FORMAT.format(nanos / 1_000_000_000);
	}
	public static long nanosToMillis(long nanos) {
		return nanos / 1_000_000;
	}
	public static String millisToPrettyString(long millis) {
		long seconds = Math.round(millis / 1000.0);
		if (seconds < 120) {
			return seconds + " seconds";
		}
		long minutes = seconds / 60;
		if (minutes >= 10) {
			return minutes + " minutes";
		}
		long remainderSeconds = seconds % 60;
		return minutes + " minutes " + remainderSeconds + " seconds";
	}
}
