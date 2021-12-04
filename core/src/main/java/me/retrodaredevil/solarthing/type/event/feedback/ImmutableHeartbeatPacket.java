package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.reason.ExecutionReason;

import static java.util.Objects.requireNonNull;

public class ImmutableHeartbeatPacket implements HeartbeatPacket {
	private final HeartbeatData data;
	private final ExecutionReason executionReason;

	@JsonCreator
	public ImmutableHeartbeatPacket(
			@JsonProperty(value = "data", required = true) HeartbeatData data,
			@JsonProperty(value = "executionReason", required = true) ExecutionReason executionReason) {
		requireNonNull(this.data = data);
		requireNonNull(this.executionReason = executionReason);
	}

	@Override
	public @NotNull HeartbeatData getData() {
		return data;
	}

	@Override
	public @NotNull ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
