package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ImmutableSuccessMateCommandPacket implements SuccessMateCommandPacket {

	private final @Nullable Integer packetVersion;
	private final MateCommand command;
	private final String source;
	private final @Nullable ExecutionReason executionReason;

	@JsonCreator
	public ImmutableSuccessMateCommandPacket(
			@JsonProperty("packetVersion") @Nullable Integer packetVersion,
			@JsonProperty(value = "command", required = true) MateCommand command,
			@JsonProperty(value = "source", required = true) String source,
			@JsonProperty("executionReason") @Nullable ExecutionReason executionReason
	) {
		this.packetVersion = packetVersion;
		this.command = requireNonNull(command);
		this.source = requireNonNull(source);
		this.executionReason = executionReason;
	}

	@Override
	public @Nullable Integer getPacketVersion() {
		return packetVersion;
	}

	// TODO remove NonNull
	@Override
	public @NonNull MateCommand getCommand() {
		return command;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getSource() {
		return source;
	}

	@Override
	public @Nullable ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
