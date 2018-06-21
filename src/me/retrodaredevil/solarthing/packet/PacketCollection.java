package me.retrodaredevil.solarthing.packet;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PacketCollection {
	private final List<SolarPacket> packets;
	private final int[] dateArray;
	private final long dateMillis;
	private final String _id;

	public PacketCollection(List<SolarPacket> packets){
		this.packets = packets;
		Calendar cal = new GregorianCalendar();
		final int month = cal.get(Calendar.MONTH) + 1; // 1 to 12 (both inclusive)
		this.dateArray = new int[] {
				cal.get(Calendar.YEAR), month, cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND)
		};
		dateMillis = cal.getTimeInMillis(); // in UTC
		this._id = "" + cal.get(Calendar.YEAR) + "," + month + "," + cal.get(Calendar.DAY_OF_MONTH) + "," +
				cal.get(Calendar.HOUR_OF_DAY) + "," + cal.get(Calendar.MINUTE) + "," + cal.get(Calendar.SECOND) + "," +
				/*cal.get(Calendar.MILLISECOND) + "," + */ Math.random(); // avoid collisions

		System.out.println("dateArray: " + Arrays.toString(dateArray));
		System.out.println("_id: " + _id);
	}
//	public static void main(String[] args){
//		Calendar cal = new GregorianCalendar();
//		System.out.println(cal.getTimeInMillis());
//		System.out.println(System.currentTimeMillis());
//	}
}
