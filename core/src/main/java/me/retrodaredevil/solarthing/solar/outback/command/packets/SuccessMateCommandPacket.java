package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.PacketWithVersion;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

@JsonTypeName("MATE_COMMAND_SUCCESS")
@JsonDeserialize(as = ImmutableSuccessMateCommandPacket.class)
@JsonExplicit
public interface SuccessMateCommandPacket extends MateCommandFeedbackPacket, PacketWithVersion {
	int VERSION_EXECUTION_REASON_INCLUDED = 2;

	int VERSION_LATEST = VERSION_EXECUTION_REASON_INCLUDED;

	@DefaultFinal
	@Override
	default @NotNull MateCommandFeedbackPacketType getPacketType() {
		return MateCommandFeedbackPacketType.MATE_COMMAND_SUCCESS;
	}

	/**
	 * Should be serialized as "command" (as a String)
	 * @return The {@link MateCommand} that was successfully sent to the MATE
	 */
	@JsonProperty("command")
	@NotNull MateCommand getCommand();

	/**
	 * Should be serialized as "source"
	 * <p>
	 * Note: Although not required, it is convention (and all existing packets as of 2021.08.31 are like this) that the string returned by this method
	 * was generated using {@link me.retrodaredevil.solarthing.DataSource#toString()}. However, there are likely a few in WMF's database with a source
	 * such as "NamedSource{command_input.txt}". There are likely very few of these, but they do exist in one database.
	 * <p>
	 * The fact that this is a string shows that this is very much legacy code. If I could redo it, I'd create a better way to communicate the data that
	 * {@link me.retrodaredevil.solarthing.DataSource} tries to communicate, without having to store it in string form. If you need more history, feel free
	 * to look around commit <a href="https://github.com/wildmountainfarms/solarthing/commit/44518ff8bdaa381739df8d3613f53808576ec6f7">44518ff8bdaa381739df8d3613f53808576ec6f7</a>
	 *
	 * @return The string representing the source of the command
	 */
	@JsonProperty("source")
	@NotNull String getSource();

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("executionReason")
	default @Nullable ExecutionReason getExecutionReason() {
		return null;
	}
}
