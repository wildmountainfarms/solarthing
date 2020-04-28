package me.retrodaredevil.solarthing.solar.daily;

public class DailyConfig {
	private final long cutOffIfStartBeforeDateMillis;
	private final long cutOffIfEndBeforeDateMillis;
//	private final long cutOffBeforeDateMillis;

	public DailyConfig(long cutOffIfStartBeforeDateMillis, long cutOffIfEndBeforeDateMillis) {
		this.cutOffIfStartBeforeDateMillis = cutOffIfStartBeforeDateMillis;
		this.cutOffIfEndBeforeDateMillis = cutOffIfEndBeforeDateMillis;
	}

	public long getCutOffIfStartBeforeDateMillis() {
		return cutOffIfStartBeforeDateMillis;
	}
	public long getCutOffIfEndBeforeDateMillis() {
		return cutOffIfEndBeforeDateMillis;
	}
}
