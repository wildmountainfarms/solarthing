package me.retrodaredevil.solarthing.config.options;

import me.retrodaredevil.solarthing.annotations.DefaultFinal;

public interface AnalyticsOption extends ProgramOptions {
	boolean isAnalyticsOptionEnabled();

	@DefaultFinal // This is the one central place that we want our implementation details in
	default boolean isAnalyticsEnabled() {
		return isAnalyticsOptionEnabled() && System.getenv("ANALYTICS_DISABLED") == null;
	}

	boolean DEFAULT_IS_ANALYTICS_ENABLED = true;
	String PROPERTY_NAME = "analytics_enabled";
}
