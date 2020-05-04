package me.retrodaredevil.solarthing.config.options;

public interface AnalyticsOption extends ProgramOptions {
	boolean isAnalyticsEnabled();

	boolean DEFAULT_IS_ANALYTICS_ENABLED = true;
	String PROPERTY_NAME = "analytics_enabled";
}
