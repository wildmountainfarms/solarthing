package me.retrodaredevil.solarthing.influxdb.retention;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ConstantRetentionPolicyGetter implements RetentionPolicyGetter {
	private final @Nullable RetentionPolicySetting retentionPolicySetting;

	public ConstantRetentionPolicyGetter(@Nullable RetentionPolicySetting retentionPolicySetting) {
		this.retentionPolicySetting = retentionPolicySetting;
	}

	@Override
	public @Nullable RetentionPolicySetting getRetentionPolicySetting() {
		return retentionPolicySetting;
	}
}
