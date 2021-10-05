package me.retrodaredevil.solarthing.type.alter.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.AlterPacketType;

import static java.util.Objects.requireNonNull;

@JsonExplicit
@JsonTypeName("SCHEDULED_COMMAND")
public class ScheduledCommandPacket implements AlterPacket {
	private final ScheduledCommandData data;
	private final ExecutionReason executionReason;

	@JsonCreator
	public ScheduledCommandPacket(
			@JsonProperty(value = "data", required = true) ScheduledCommandData data,
			@JsonProperty(value = "executionReason", required = true) ExecutionReason executionReason) {
		requireNonNull(this.data = data);
		requireNonNull(this.executionReason = executionReason);
	}

	@Override
	public @NotNull AlterPacketType getPacketType() {
		return AlterPacketType.SCHEDULED_COMMAND;
	}

	@JsonProperty("data")
	public @NotNull ScheduledCommandData getData() {
		return data;
	}

	@JsonProperty("executionReason")
	public @NotNull ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
