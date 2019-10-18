package me.retrodaredevil.solarthing.influxdb.retention;

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
