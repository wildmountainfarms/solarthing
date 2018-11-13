package me.retrodaredevil.solarthing.packet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

public class PacketCollection {
	private final List<SolarPacket> packets;
	/** An array that represents the date [year, day, month, hour, minute, second, millisecond] (local time)*/
	private final int[] dateArray;
	/** The UTC date represented in milliseconds */
	private final long dateMillis;
	/** A special field that's used when serializing an object into a couchdb database */
	private final String _id;

	/**
	 * Creates a new PacketCollection
	 * <p>
	 * If packets is mutated after this constructor is called, it will have no effect; it is tolerated
	 * @param packets The packets collection
	 */
	public PacketCollection(Collection<SolarPacket> packets){
		this.packets = new ArrayList<>(packets);
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
	}
}
