package me.retrodaredevil.solarthing.util.time;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DailyIdentifier implements TimeIdentifier {
	private final TimeZone timeZone;


	public DailyIdentifier(){
		this(TimeZone.getDefault());
	}
	public DailyIdentifier(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public long getTimeId(long timeMillis) {
		Calendar calendar = new GregorianCalendar(timeZone);
		calendar.setTimeInMillis(timeMillis);
		return 400 * calendar.get(Calendar.YEAR) + calendar.get(Calendar.DAY_OF_YEAR);
	}
}
