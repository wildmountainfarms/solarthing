package me.retrodaredevil.solarthing.packets.collection;

import java.time.ZonedDateTime;

public interface PacketCollectionIdGenerator {
	String generateId(ZonedDateTime zonedDateTime);

	class Defaults {
		public static final PacketCollectionIdGenerator UNIQUE_GENERATOR = zonedDateTime -> {
			final int year = zonedDateTime.getYear();
			final int month = zonedDateTime.getMonth().getValue();
			final int day = zonedDateTime.getDayOfMonth();
			final int hour = zonedDateTime.getHour();
			final int minute = zonedDateTime.getMinute();
			final int second = zonedDateTime.getSecond();
			return "" + year + "," + month + "," + day + "," +
					hour + "," + minute + "," + second + "," + Math.random(); // avoid collisions
		};
	}
}
