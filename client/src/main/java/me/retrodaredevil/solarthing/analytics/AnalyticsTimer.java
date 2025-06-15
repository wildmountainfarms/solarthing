package me.retrodaredevil.solarthing.analytics;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class AnalyticsTimer {
	private State state = State.SEND_FIRST;
	private Long startTimeNanos = null;
	private long nextSendNanos = Long.MIN_VALUE;

	public boolean shouldSend() {
		return System.nanoTime() >= nextSendNanos;
	}
	public int getUptimeHours() {
		long uptimeNanos = System.nanoTime() - requireNonNull(startTimeNanos);
		return (int) Math.round(Duration.ofNanos(uptimeNanos).toMinutes() / 60.0);
	}
	public void onSend() {
		long nowNanos = System.nanoTime();
		switch (requireNonNull(state)) {
			case SEND_FIRST -> {
				state = State.SEND_ONE_HOUR;
				nextSendNanos = nowNanos + Duration.ofMinutes(60).toNanos();
				startTimeNanos = nowNanos;
			}
			case SEND_ONE_HOUR, SEND_DAILY -> {
				state = State.SEND_DAILY;
				nextSendNanos = nowNanos + Duration.ofHours(20).toNanos();
			}
			default -> throw new AssertionError("Unknown state: " + state);
		}
	}
	private enum State {
		SEND_FIRST,
		SEND_ONE_HOUR,
		SEND_DAILY
	}
}
