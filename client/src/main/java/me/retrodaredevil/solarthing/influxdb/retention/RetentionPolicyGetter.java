package me.retrodaredevil.solarthing.influxdb.retention;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface RetentionPolicyGetter {
	/**
	 * @return The {@link RetentionPolicySetting} to use or null
	 */
	@Nullable RetentionPolicySetting getRetentionPolicySetting();
}
