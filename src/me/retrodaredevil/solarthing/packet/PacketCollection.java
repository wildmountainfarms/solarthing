package me.retrodaredevil.solarthing.packet;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PacketCollection {
	private final List<SolarPacket> packets;
	private final int[] dateArray;
	private final long dateMillis;
	/** A special field that's used when serializing an object into a couchdb database */
	private final String _id;

	public PacketCollection(List<SolarPacket> packets){
		this.packets = packets;
		final Calendar cal = new GregorianCalendar();
		final int year = cal.get(Calendar.YEAR);
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int month = cal.get(Calendar.MONTH) + 1; // [1..12]
		final int hour = cal.get(Calendar.HOUR);
		final int minute = cal.get(Calendar.MINUTE);
		final int second = cal.get(Calendar.SECOND);
		final int millisecond = cal.get(Calendar.MILLISECOND);
		this.dateArray = new int[] {
				year, month, day,
				hour, minute, second, millisecond
		};
		dateMillis = cal.getTimeInMillis(); // in UTC
		this._id = "" + year + "," + month + "," + day + "," +
				hour + "," + minute + "," + second + "," + Math.random(); // avoid collisions

//		System.out.println("dateArray: " + Arrays.toString(dateArray));
//		System.out.println("_id: " + _id);
	}
}
