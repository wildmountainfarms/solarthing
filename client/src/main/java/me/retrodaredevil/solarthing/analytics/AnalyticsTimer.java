package me.retrodaredevil.solarthing.analytics;

import static java.util.Objects.requireNonNull;

public class AnalyticsTimer {
	private State state = State.SEND_FIRST;
	private Long startTime = null;
	private long nextSend = 0;

	public boolean shouldSend() {
		return System.currentTimeMillis() >= nextSend;
	}
	public int getUptimeHours() {
		long uptimeMillis = System.currentTimeMillis() - requireNonNull(startTime);
		return (int) Math.round(uptimeMillis / (60 * 60 * 1000.0));
	}
	public void onSend() {
		long now = System.currentTimeMillis();
		switch(requireNonNull(state)) {
			case SEND_FIRST:
				state = State.SEND_ONE_HOUR;
				nextSend = now + 60 * 60 * 1000;
				startTime = now;
				break;
			case SEND_ONE_HOUR:
			case SEND_DAILY:
				state = State.SEND_DAILY;
				nextSend = now + 20 * 60 * 60 * 1000;
				break;
			default:
				throw new AssertionError("Unknown state: " + state);
		}
	}
	private enum State {
		SEND_FIRST,
		SEND_ONE_HOUR,
		SEND_DAILY
	}
}
