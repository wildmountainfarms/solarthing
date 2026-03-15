package me.retrodaredevil.solarthing.influxdb.retention;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ConstantRetentionPolicyGetter implements RetentionPolicyGetter {
	private final RetentionPolicySetting retentionPolicySetting;

	public ConstantRetentionPolicyGetter(RetentionPolicySetting retentionPolicySetting) {
		this.retentionPolicySetting = retentionPolicySetting;
	}

	@Override
	public RetentionPolicySetting getRetentionPolicySetting() {
		return retentionPolicySetting;
	}
}
