package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import static java.util.Objects.requireNonNull;

public class ImmutableSuccessMateCommandPacket implements SuccessMateCommandPacket {

	private final Integer packetVersion;
	private final MateCommand command;
	private final String source;
	private final ExecutionReason executionReason;

	@JsonCreator
	public ImmutableSuccessMateCommandPacket(
			@JsonProperty("packetVersion") Integer packetVersion,
			@JsonProperty(value = "command", required = true) MateCommand command,
			@JsonProperty(value = "source", required = true) String source,
			@JsonProperty("executionReason") ExecutionReason executionReason
	) {
		this.packetVersion = packetVersion;
		this.command = requireNonNull(command);
		this.source = requireNonNull(source);
		this.executionReason = executionReason;
	}

	@Override
	public Integer getPacketVersion() {
		return packetVersion;
	}

	@Override
	public @NotNull MateCommand getCommand() {
		return command;
	}

	@Override
	public @NotNull String getSource() {
		return source;
	}

	@Override
	public @Nullable ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
