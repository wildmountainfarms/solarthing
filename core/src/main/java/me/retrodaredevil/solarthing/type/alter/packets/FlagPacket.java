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

/**
 * Represents data for a flag stored in the alter database. A flag only has a name, and data associated with it to
 * denote when the flag is active. Flags that will be inactive for the rest of time should be removed, but they may not be removed
 * immediately, so you should always check {@link me.retrodaredevil.solarthing.type.alter.flag.ActivePeriod#isActive(long) ActivePeriod.isActive()} before assuming a flag is active
 * <p>
 * In the future, it may be beneficial to display some flag packets (e.g. as notifications in solarthing-android). It is likely that
 * some flags will persist in solarthing_alter as some sort of configuration. Because of this it might be beneficial to know which packets
 * are volatile. This packet does not have any metadata except for {@link #getExecutionReason()}, which can be used along with flagPacket.{@link #getFlagData() getFlagData()}.{@link FlagData#getActivePeriod() getActivePeriod()}
 * to determine if this given flag is volatile and worth displaying
 */
@JsonTypeName("FLAG")
@JsonExplicit
public final class FlagPacket implements AlterPacket {
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
