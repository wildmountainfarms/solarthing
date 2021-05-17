package me.retrodaredevil.solarthing.solar.accumulation;

public final class AccumulationConfig {
	private final long cutOffIfStartBeforeDateMillis;
	private final long cutOffIfEndBeforeDateMillis;

	public AccumulationConfig(long cutOffIfStartBeforeDateMillis, long cutOffIfEndBeforeDateMillis) {
		this.cutOffIfStartBeforeDateMillis = cutOffIfStartBeforeDateMillis;
		this.cutOffIfEndBeforeDateMillis = cutOffIfEndBeforeDateMillis;
	}
	public static AccumulationConfig createDefault(long dayStartTimeMillis) {
		return new AccumulationConfig(dayStartTimeMillis + 3 * 60 * 60 * 1000, dayStartTimeMillis + 10 * 60 * 60 * 1000);
	}

	/**
	 * If the first packet's dateMillis is before this value, it can be assumed that data from the first packet is actually
	 * data accumulated from the previous day.
	 * @return The date in millis for the "cut off if start is before"
	 */
	public long getCutOffIfStartBeforeDateMillis() {
		return cutOffIfStartBeforeDateMillis;
	}
	/**
	 * If the end packet's dateMillis is before this value, it can be assumed that data from the first packet is actually
	 * data accumulated from the previous day.
	 * <p>
	 * Note that the effect of this can actually change (unlike the effect of {@link #getCutOffIfStartBeforeDateMillis()}). This is because
	 * if packets keep coming in from data that is assumed to not be cut off (corresponding to {@link AccumulationPair.StartPacketType#MIDDLE_OF_DAY_CONNECT}),
	 * that can actually change if the end packet ends up becoming after this returned value. This rarely if ever happens, and is basically like a
	 * "hey, we're actually getting a ton of data, and this hasn't reset yet, so all the data is probably from today" -- not "cut off".
	 * @return The date in millis for the "cut off if end is before"
	 */
	public long getCutOffIfEndBeforeDateMillis() {
		return cutOffIfEndBeforeDateMillis;
	}
}
