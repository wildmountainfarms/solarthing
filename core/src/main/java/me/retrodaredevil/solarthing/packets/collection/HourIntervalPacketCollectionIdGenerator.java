package me.retrodaredevil.solarthing.packets.collection;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.ZonedDateTime;
import java.util.Base64;

/**
 * A {@link PacketCollectionIdGenerator} that generates a certain number of unique IDs per hour.
 */
public final class HourIntervalPacketCollectionIdGenerator implements PacketCollectionIdGenerator {
	private static final Base64.Encoder UNIQUE_ENCODER = Base64.getEncoder().withoutPadding();

	private final int uniqueIdsInOneHour;
	private final Integer uniqueCode;
	private final String uniqueCodeEncoded;
	private final boolean shortVersion;

	public HourIntervalPacketCollectionIdGenerator(int uniqueIdsInOneHour, Integer uniqueCode) {
		this(uniqueIdsInOneHour, uniqueCode, false);
	}
	/**
	 * @param uniqueIdsInOneHour The number of unique ids in an hour.
	 */
	public HourIntervalPacketCollectionIdGenerator(int uniqueIdsInOneHour, Integer uniqueCode, boolean shortVersion) {
		this.uniqueIdsInOneHour = uniqueIdsInOneHour;
		this.uniqueCode = uniqueCode;
		uniqueCodeEncoded = uniqueCode == null ? null : encodeUniqueCode(uniqueCode);
		this.shortVersion = shortVersion;
		if(uniqueIdsInOneHour <= 0){
			throw new IllegalArgumentException("uniqueIdsInOneHour cannot be <= 0. It is: " + uniqueIdsInOneHour);
		}
	}
	static String encodeUniqueCode(int uniqueCode) {
		return UNIQUE_ENCODER.encodeToString(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(uniqueCode).array());
	}

	@Override
	public String generateId(ZonedDateTime zonedDateTime) {
		final int year = zonedDateTime.getYear();
		final int month = zonedDateTime.getMonth().getValue();
		final int day = zonedDateTime.getDayOfMonth();
		final int hour = zonedDateTime.getHour();
		final int minute = zonedDateTime.getMinute();
		final int second = zonedDateTime.getSecond();
		final int millisecond = zonedDateTime.getNano() / 1_000_000;
		final double percent = (millisecond + 1000 * (second + (60 * minute))) / (1000.0 * 60.0 * 60.0);
		final int progressNumber = (int) (percent * uniqueIdsInOneHour) + 1;

		String uniqueIdsString = "" + uniqueIdsInOneHour;
		if (shortVersion) {
			int shortYear = year % 1000;
			String r = String.format("%02d%02d%02d,%02d,%0" + uniqueIdsString.length() + "d/" + uniqueIdsString, shortYear, month, day, hour, progressNumber);
			if (uniqueCode != null) {
				r += "|" + uniqueCodeEncoded;
			}
			return r;
		} else {
			String r = String.format("%d,%02d,%02d,%02d,(%0" + uniqueIdsString.length() + "d/" + uniqueIdsString + ")", year, month, day, hour, progressNumber);
			if (uniqueCode != null) {
				r += String.format(",[%08x]", uniqueCode);
			}
			return r;
		}
	}
}
