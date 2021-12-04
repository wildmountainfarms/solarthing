package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;

import java.time.Duration;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public final class HeartbeatData implements UniqueStringRepresentation {

	private final String displayName;
	private final String identifier;
	private final Duration expectedDurationToNextHeartbeat;
	private final Duration bufferDuration;

	public HeartbeatData(
			String displayName,
			String identifier,
			Duration expectedDurationToNextHeartbeat,
			Duration bufferDuration) {
		requireNonNull(this.displayName = displayName);
		requireNonNull(this.identifier = identifier);
		requireNonNull(this.expectedDurationToNextHeartbeat = expectedDurationToNextHeartbeat);
		requireNonNull(this.bufferDuration = bufferDuration);
	}
	@JsonCreator
	public HeartbeatData(
			@JsonProperty(value = "displayName", required = true) String displayName,
			@JsonProperty(value = "identifier", required = true) String identifier,
			@JsonProperty(value = "expectedDurationToNextHeartbeat", required = true) String expectedDurationToNextHeartbeat,
			@JsonProperty(value = "bufferDuration", required = true) String bufferDuration) {
		this(displayName, identifier, Duration.parse(expectedDurationToNextHeartbeat), Duration.parse(bufferDuration));
	}


	@JsonProperty("displayName")
	public @NotNull String getDisplayName() {
		return displayName;
	}

	@JsonProperty("identifier")
	public @NotNull String getIdentifier() {
		return identifier;
	}

	// Note that the serialization of this relies on the jsr310 jackson module
	@JsonProperty("expectedDurationToNextHeartbeat")
	public @NotNull Duration getExpectedDurationToNextHeartbeat() {
		return expectedDurationToNextHeartbeat;
	}

	/**
	 * @return The duration that is allowed after {@link #getExpectedDurationToNextHeartbeat()} before an alert should be sent
	 */
	@JsonProperty("bufferDuration")
	public @NotNull Duration getBufferDuration() {
		return bufferDuration;
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public @NotNull String getUniqueString() {
		return "HeartbeatData(" +
				"displayName='" + displayName + '\'' +
				", identifier='" + identifier + '\'' +
				", expectedDurationToNextHeartbeat=" + expectedDurationToNextHeartbeat +
				", bufferDuration=" + bufferDuration +
				')';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HeartbeatData that = (HeartbeatData) o;
		return displayName.equals(that.displayName) && identifier.equals(that.identifier) && expectedDurationToNextHeartbeat.equals(that.expectedDurationToNextHeartbeat) && bufferDuration.equals(that.bufferDuration);
	}

	@Override
	public int hashCode() {
		return Objects.hash(displayName, identifier, expectedDurationToNextHeartbeat, bufferDuration);
	}
}
