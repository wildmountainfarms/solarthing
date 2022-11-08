package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public abstract class RequestProgramOptionsBase extends PacketHandlingOptionBase implements AnalyticsOption, CommandOption, ActionsOption {
	@JsonProperty(AnalyticsOption.PROPERTY_NAME)
	private boolean isAnalyticsEnabled = AnalyticsOption.DEFAULT_IS_ANALYTICS_ENABLED;

	// When defined as a Duration, Jackson will parse numbers as second values for the duration
	@JsonProperty("period")
	private Duration period = Duration.ofSeconds(5);
	@JsonProperty("minimum_wait")
	private Duration minimumWait = Duration.ofSeconds(1);

	@JsonProperty("commands")
	private List<CommandConfig> commandConfigs;

	@JsonProperty("actions")
	private List<File> actionNodeFiles = new ArrayList<>();

	@Override
	public boolean isAnalyticsOptionEnabled() {
		return isAnalyticsEnabled;
	}

	public Duration getPeriod() {
		return requireNonNull(period);
	}
	public Duration getMinimumWait() {
		return requireNonNull(minimumWait);
	}

	@Override
	public @Nullable List<CommandConfig> getDeclaredCommandsNullable() {
		return commandConfigs;
	}

	@Override
	public List<File> getActionNodeFiles() {
		return requireNonNull(actionNodeFiles, "You cannot use a null value for the actions property! Use an empty array or leave it undefined.");
	}
}
