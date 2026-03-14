package me.retrodaredevil.solarthing.type.alter.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.AlterPacketType;
import me.retrodaredevil.solarthing.type.alter.flag.FlagAliasData;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@JsonTypeName("FLAG_ALIAS")
@JsonExplicit
@NullMarked
public final class FlagAliasPacket implements AlterPacket {

	private final FlagAliasData flagData;
	private final ExecutionReason executionReason;

	@JsonCreator
	public FlagAliasPacket(
			@JsonProperty(value = "flagAliasData", required = true) FlagAliasData flagData,
			@JsonProperty(value = "executionReason", required = true) ExecutionReason executionReason) {
		this.flagData = requireNonNull(flagData);
		this.executionReason = requireNonNull(executionReason);
	}
	@Override
	public AlterPacketType getPacketType() {
		return AlterPacketType.FLAG_ALIAS;
	}

	@JsonProperty("flagAliasData")
	public FlagAliasData getFlagAliasData() {
		return flagData;
	}

	@JsonProperty("executionReason")
	public ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
