package me.retrodaredevil.solarthing.influxdb.retention;

import org.jspecify.annotations.Nullable;

public interface RetentionPolicyGetter {
	/**
	 * @return The {@link RetentionPolicySetting} to use or null
	 */
	@Nullable RetentionPolicySetting getRetentionPolicySetting();
}
