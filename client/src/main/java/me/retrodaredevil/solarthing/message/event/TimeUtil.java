package me.retrodaredevil.solarthing.message.event;

public final class TimeUtil {
	private TimeUtil() { throw new UnsupportedOperationException(); }
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
