package me.retrodaredevil.solarthing.influxdb.retention;

import me.retrodaredevil.solarthing.util.frequency.FrequentHandler;
import me.retrodaredevil.solarthing.util.frequency.FrequentObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FrequentRetentionPolicyGetter implements RetentionPolicyGetter {
	private final FrequentHandler<RetentionPolicySetting> handler;

	private int lastMillis = 0;

	public FrequentRetentionPolicyGetter(FrequentHandler<RetentionPolicySetting> handler) {
		this.handler = handler;
	}

	@Override
	public RetentionPolicySetting getRetentionPolicySetting() {
		Calendar calendar = new GregorianCalendar();
		int millis = calendar.get(Calendar.MINUTE) * 60 * 1000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);
		if(lastMillis > millis){
			handler.reset();
		}
		lastMillis = millis;
		double progress = millis / (60.0 * 60.0 * 1000.0);
		FrequentObject<RetentionPolicySetting> object = handler.get(progress);
		handler.use(object);
		return object.getObject();
	}
}
