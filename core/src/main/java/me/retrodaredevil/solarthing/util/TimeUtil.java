package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.text.DecimalFormat;
import java.text.Format;
import java.time.Duration;
import java.time.format.DateTimeParseException;

@UtilityClass
public class TimeUtil {
	private TimeUtil() { throw new UnsupportedOperationException(); }

	private static final Format SECONDS_FORMAT = new DecimalFormat("0.000");

	public static String nanosToSecondsString(long nanos) {
		return SECONDS_FORMAT.format(nanos / 1_000_000_000.0);
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

	public static String informalDurationToFormal(String informalDuration) {
		String formalDuration = informalDuration.toUpperCase()
				.replaceAll("SECONDS|SECOND", "S")
				.replaceAll("MINUTES|MINUTE", "M")
				.replaceAll("HOURS|HOUR", "H")
				.replaceAll("DAYS|DAY", "D")
				;
		if (!informalDuration.startsWith("P")) { // Make the format of the string we send lenient
			if (informalDuration.contains("T")) {
				formalDuration = "P" + formalDuration;
			} else {
				formalDuration = "PT" + formalDuration;
			}
		}
		formalDuration = formalDuration.replaceAll(" ", "");
		return formalDuration;
	}
	public static @Nullable Duration lenientParseDurationOrNull(String informalDuration) {
		String formalDuration = informalDurationToFormal(informalDuration);
		try {
			return Duration.parse(formalDuration);
		} catch (DateTimeParseException ignored) {}
		return null;
	}
}
