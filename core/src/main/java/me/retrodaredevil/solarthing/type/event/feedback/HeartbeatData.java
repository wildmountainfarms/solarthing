package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;
import org.jspecify.annotations.NonNull;

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
		this.displayName = requireNonNull(displayName);
		this.identifier = requireNonNull(identifier);
		this.expectedDurationToNextHeartbeat = requireNonNull(expectedDurationToNextHeartbeat);
		this.bufferDuration = requireNonNull(bufferDuration);
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
	public @NonNull String getDisplayName() {
		return displayName;
	}

	@JsonProperty("identifier")
	public @NonNull String getIdentifier() {
		return identifier;
	}

	// Note that the serialization of this relies on the jsr310 jackson module
	@JsonProperty("expectedDurationToNextHeartbeat")
	public @NonNull Duration getExpectedDurationToNextHeartbeat() {
		return expectedDurationToNextHeartbeat;
	}

	/**
	 * @return The duration that is allowed after {@link #getExpectedDurationToNextHeartbeat()} before an alert should be sent
	 */
	@JsonProperty("bufferDuration")
	public @NonNull Duration getBufferDuration() {
		return bufferDuration;
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public @NonNull String getUniqueString() {
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
