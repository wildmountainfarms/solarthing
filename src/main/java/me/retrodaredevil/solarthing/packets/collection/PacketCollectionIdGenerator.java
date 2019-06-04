package me.retrodaredevil.solarthing.packets.collection;

import java.util.Calendar;

public interface PacketCollectionIdGenerator {
	String generateId(Calendar calendar);
	
	class Defaults {
		public static final PacketCollectionIdGenerator UNIQUE_GENERATOR = cal -> {
			final int year = cal.get(Calendar.YEAR);
			final int month = cal.get(Calendar.MONTH) + 1; // [1..12]
			final int day = cal.get(Calendar.DAY_OF_MONTH);
			final int hour = cal.get(Calendar.HOUR_OF_DAY);
			final int minute = cal.get(Calendar.MINUTE);
			final int second = cal.get(Calendar.SECOND);
//			final int millisecond = cal.get(Calendar.MILLISECOND);
			return "" + year + "," + month + "," + day + "," +
					hour + "," + minute + "," + second + "," + Math.random(); // avoid collisions
		};
	}
}
