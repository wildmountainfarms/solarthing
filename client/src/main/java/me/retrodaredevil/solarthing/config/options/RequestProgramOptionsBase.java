package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class RequestProgramOptionsBase extends PacketHandlingOptionBase implements AnalyticsOption {
	@JsonProperty(AnalyticsOption.PROPERTY_NAME)
	private boolean isAnalyticsEnabled = AnalyticsOption.DEFAULT_IS_ANALYTICS_ENABLED;

	@JsonProperty("period")
	private float periodSeconds = 5.0f;
	@JsonProperty("minimum_wait")
	private float minimumWaitSeconds = 1.0f;

	@Override
	public boolean isAnalyticsOptionEnabled() {
		return isAnalyticsEnabled;
	}

	public long getPeriod() {
		return Math.round(periodSeconds * 1000.0f);
	}
	public long getMinimumWait() {
		return Math.round(minimumWaitSeconds * 1000.0f);
	}
}
