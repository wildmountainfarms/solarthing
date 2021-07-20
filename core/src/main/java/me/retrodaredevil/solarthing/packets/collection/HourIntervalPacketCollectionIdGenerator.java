package me.retrodaredevil.solarthing.packets.collection;

import java.time.ZonedDateTime;

/**
 * A {@link PacketCollectionIdGenerator} that generates a certain number of unique IDs per hour.
 */
public final class HourIntervalPacketCollectionIdGenerator implements PacketCollectionIdGenerator {

	private final int uniqueIdsInOneHour;
	private final Integer uniqueCode;

	/**
	 * @param uniqueIdsInOneHour The number of unique ids in an hour.
	 */
	public HourIntervalPacketCollectionIdGenerator(int uniqueIdsInOneHour, Integer uniqueCode) {
		this.uniqueIdsInOneHour = uniqueIdsInOneHour;
		this.uniqueCode = uniqueCode;
		if(uniqueIdsInOneHour <= 0){
			throw new IllegalArgumentException("uniqueIdsInOneHour cannot be <= 0. It is: " + uniqueIdsInOneHour);
		}
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

		String uniqueIdsString = "" + uniqueIdsInOneHour;
		String r = String.format("%d,%02d,%02d,%02d,(%0" + uniqueIdsString.length() + "d/" + uniqueIdsString + ")", year, month, day, hour, (int) (percent * uniqueIdsInOneHour) + 1);
		if(uniqueCode != null){
			r += String.format(",[%08x]", uniqueCode);
		}
		return r;
	}
}
