package me.retrodaredevil.solarthing.influxdb.retention;

import me.retrodaredevil.solarthing.util.frequency.FrequentHandler;
import me.retrodaredevil.solarthing.util.frequency.FrequentObject;

public class FrequentRetentionPolicyGetter implements RetentionPolicyGetter {
	private final FrequentHandler<RetentionPolicySetting> handler;

	private Long startMillis = null;
	private double lastProgress = 0;

	public FrequentRetentionPolicyGetter(FrequentHandler<RetentionPolicySetting> handler) {
		this.handler = handler;
	}

	@Override
	public RetentionPolicySetting getRetentionPolicySetting() {
		long now = System.currentTimeMillis();
		Long startMillis = this.startMillis;
		if(startMillis == null){
			startMillis = now;
			this.startMillis = now;
		}
		long elapsed = now - startMillis;
		double progress = (elapsed / (60.0 * 60.0 * 1000.0)) % 1;
		if(lastProgress > progress){
			handler.reset();
		}
		lastProgress = progress;
		FrequentObject<RetentionPolicySetting> object = handler.get(progress);
		if(object == null){
			return null;
		}
		handler.use(object);
		return object.getObject();
	}
}
