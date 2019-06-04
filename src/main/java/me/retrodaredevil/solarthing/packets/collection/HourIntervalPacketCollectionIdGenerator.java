package me.retrodaredevil.solarthing.packets.collection;

import java.util.Calendar;

public final class HourIntervalPacketCollectionIdGenerator implements PacketCollectionIdGenerator {
	
	private final int uniqueIdsInOneHour;
	
	/**
	 * @param uniqueIdsInOneHour The number of unique ids in an hour.
	 */
	public HourIntervalPacketCollectionIdGenerator(int uniqueIdsInOneHour) {
		this.uniqueIdsInOneHour = uniqueIdsInOneHour;
	}
	
	@Override
	public String generateId(Calendar cal) {
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH) + 1; // [1..12]
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		final int minute = cal.get(Calendar.MINUTE);
		final int second = cal.get(Calendar.SECOND);
		final int millisecond = cal.get(Calendar.MILLISECOND);
		final double percent = (millisecond + 1000 * (second + (60 * minute))) / (1000.0 * 60.0 * 60.0);
		return "" + year + "," + month + "," + day + "," +
			hour + ",(hour interval: " + ((int) (percent * uniqueIdsInOneHour) + 1) + "/" + uniqueIdsInOneHour + ")";
	}
}
