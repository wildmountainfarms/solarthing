package me.retrodaredevil.solarthing.type.alter.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.AlterPacketType;
import me.retrodaredevil.solarthing.type.alter.flag.FlagAliasData;

import static java.util.Objects.requireNonNull;

@JsonTypeName("FLAG_ALIAS")
@JsonExplicit
public final class FlagAliasPacket implements AlterPacket {

	private final FlagAliasData flagData;
	private final ExecutionReason executionReason;

	@JsonCreator
	public FlagAliasPacket(
			@JsonProperty(value = "flagAliasData", required = true) FlagAliasData flagData,
			@JsonProperty(value = "executionReason", required = true) ExecutionReason executionReason) {
		requireNonNull(this.flagData = flagData);
		requireNonNull(this.executionReason = executionReason);
	}
	@Override
	public @NotNull AlterPacketType getPacketType() {
		return AlterPacketType.FLAG;
	}

	@JsonProperty("flagAliasData")
	public @NotNull FlagAliasData getFlagAliasData() {
		return flagData;
	}

	@JsonProperty("executionReason")
	public @NotNull ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
