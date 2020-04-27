package me.retrodaredevil.solarthing.solar.daily;

public class DailyConfig {
	private final long cutOffBeforeDateMillis;

	public DailyConfig(long cutOffBeforeDateMillis) {
		this.cutOffBeforeDateMillis = cutOffBeforeDateMillis;
	}

	public long getCutOffBeforeDateMillis() {
		return cutOffBeforeDateMillis;
	}
}
