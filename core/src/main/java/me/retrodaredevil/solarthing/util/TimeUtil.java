package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.text.DecimalFormat;
import java.text.Format;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Locale;

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
		String formalDuration = informalDuration.toUpperCase(Locale.ENGLISH)
				.replaceAll("SECONDS|SECOND", "S")
				.replaceAll("MINUTES|MINUTE", "M")
				.replaceAll("HOURS|HOUR", "H")
				.replaceAll("DAYS|DAY", "D")
				.replaceAll("AND", "")
				;
		if (!formalDuration.startsWith("P")) { // Make the format of the string we send lenient
			if (formalDuration.contains("T")) {
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

	public static String instantToSlackDateSeconds(Instant instant) {
		return instantToSlackDateSeconds(instant, instant.toString());
	}
	public static String instantToSlackDateSeconds(Instant instant, String fallback) {
		// https://api.slack.com/reference/surfaces/formatting#date-formatting
		long seconds = instant.getEpochSecond();
		return "<!date^" + seconds + "^{date_num} {time_secs}|" + slackEscape(fallback) + ">";
	}
	public static String slackEscape(String messageToEscape) {
		// https://api.slack.com/reference/surfaces/formatting#escaping
		return messageToEscape
				.replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				;
	}
}
