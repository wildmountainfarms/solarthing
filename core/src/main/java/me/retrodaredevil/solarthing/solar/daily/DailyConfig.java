package me.retrodaredevil.solarthing.solar.daily;

public final class DailyConfig {
	private final long cutOffIfStartBeforeDateMillis;
	private final long cutOffIfEndBeforeDateMillis;

	public DailyConfig(long cutOffIfStartBeforeDateMillis, long cutOffIfEndBeforeDateMillis) {
		this.cutOffIfStartBeforeDateMillis = cutOffIfStartBeforeDateMillis;
		this.cutOffIfEndBeforeDateMillis = cutOffIfEndBeforeDateMillis;
	}
	public static DailyConfig createDefault(long dayStartTimeMillis) {
		return new DailyConfig(dayStartTimeMillis + 3 * 60 * 60 * 1000, dayStartTimeMillis + 10 * 60 * 60 * 1000);
	}

	public long getCutOffIfStartBeforeDateMillis() {
		return cutOffIfStartBeforeDateMillis;
	}
	public long getCutOffIfEndBeforeDateMillis() {
		return cutOffIfEndBeforeDateMillis;
	}
}
