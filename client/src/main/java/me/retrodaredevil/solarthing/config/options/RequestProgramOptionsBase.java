package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

public abstract class RequestProgramOptionsBase extends PacketHandlingOptionBase implements CommandOption, ActionsOption {

	// When defined as a Duration, Jackson will parse numbers as second values for the duration
	@JsonProperty("period")
	private Duration period = Duration.ofSeconds(5);
	@JsonProperty("minimum_wait")
	private Duration minimumWait = Duration.ofSeconds(1);

	public Duration getPeriod() {
		return requireNonNull(period);
	}
	public Duration getMinimumWait() {
		return requireNonNull(minimumWait);
	}

}
