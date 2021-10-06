package me.retrodaredevil.solarthing.type.alter.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.AlterPacketType;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;

import static java.util.Objects.requireNonNull;

@JsonTypeName("FLAG")
@JsonExplicit
public class FlagPacket implements AlterPacket {
	private final FlagData flagData;
	private final ExecutionReason executionReason;

	@JsonCreator
	public FlagPacket(
			@JsonProperty(value = "flagData", required = true) FlagData flagData,
			@JsonProperty(value = "executionReason", required = true) ExecutionReason executionReason) {
		requireNonNull(this.flagData = flagData);
		requireNonNull(this.executionReason = executionReason);
	}
	@Override
	public @NotNull AlterPacketType getPacketType() {
		return AlterPacketType.FLAG;
	}

	@JsonProperty("flagData")
	public @NotNull FlagData getFlagData() {
		return flagData;
	}

	@JsonProperty("executionReason")
	public @NotNull ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
