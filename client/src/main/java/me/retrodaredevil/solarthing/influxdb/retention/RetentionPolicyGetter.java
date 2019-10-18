package me.retrodaredevil.solarthing.influxdb.retention;

public interface RetentionPolicyGetter {
	/**
	 * @return The {@link RetentionPolicySetting} to use or null
	 */
	RetentionPolicySetting getRetentionPolicySetting();
}
