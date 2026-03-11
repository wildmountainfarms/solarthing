package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public class ImmutableHeartbeatPacket implements HeartbeatPacket {
	private final HeartbeatData data;
	private final ExecutionReason executionReason;

	@JsonCreator
	public ImmutableHeartbeatPacket(
			@JsonProperty(value = "data", required = true) HeartbeatData data,
			@JsonProperty(value = "executionReason", required = true) ExecutionReason executionReason) {
		this.data = requireNonNull(data);
		this.executionReason = requireNonNull(executionReason);
	}

	@Override
	public @NonNull HeartbeatData getData() {
		return data;
	}

	@Override
	public @NonNull ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
