package me.retrodaredevil.solarthing.influxdb.retention;

public interface RetentionPolicyGetter {
	RetentionPolicySetting getRetentionPolicySetting();
}
