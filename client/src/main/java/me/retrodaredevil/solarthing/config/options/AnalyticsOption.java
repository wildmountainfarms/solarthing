package me.retrodaredevil.solarthing.config.options;

public interface AnalyticsOption extends ProgramOptions {
	boolean isAnalyticsOptionEnabled();

	default boolean isAnalyticsEnabled() {
		return isAnalyticsOptionEnabled() && System.getenv("ANALYTICS_DISABLED") == null;
	}

	boolean DEFAULT_IS_ANALYTICS_ENABLED = true;
	String PROPERTY_NAME = "analytics_enabled";
}
