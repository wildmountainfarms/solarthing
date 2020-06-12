package me.retrodaredevil.solarthing.util.time;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DailyIdentifier implements TimeIdentifier {
	private final Calendar calendar;


	public DailyIdentifier(TimeZone timeZone) {
		calendar = new GregorianCalendar(timeZone);
	}

	@Override
	public long getTimeId(long timeMillis) {
		synchronized (calendar) {
			calendar.setTimeInMillis(timeMillis);
			return 400 * calendar.get(Calendar.YEAR) + calendar.get(Calendar.DAY_OF_YEAR); // this is arbitrary, so using 400 is fine
		}
	}
}
