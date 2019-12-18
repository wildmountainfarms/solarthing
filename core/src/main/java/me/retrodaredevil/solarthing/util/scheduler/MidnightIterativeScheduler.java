package me.retrodaredevil.solarthing.util.scheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MidnightIterativeScheduler implements IterativeScheduler {
	private final TimeZone timeZone;

	private Integer lastDay = null;

	public MidnightIterativeScheduler(){
		this(TimeZone.getDefault());
	}
	public MidnightIterativeScheduler(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	private int getDay(){
		return new GregorianCalendar(timeZone).get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public boolean shouldRun() {
		int day = getDay();
		Integer lastDay = this.lastDay;
		if(lastDay == null){
			this.lastDay = day;
			lastDay = day;
		}
		boolean r = lastDay != day;
		this.lastDay = day;
		return r;
	}
}
