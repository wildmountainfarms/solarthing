package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import javax.validation.constraints.NotNull;

@JsonTypeName("MATE_COMMAND_SUCCESS")
@JsonDeserialize(as = ImmutableSuccessMateCommandPacket.class)
@JsonExplicit
public interface SuccessMateCommandPacket extends MateCommandFeedbackPacket {
	@NotNull
    @Override
	default MateCommandFeedbackPacketType getPacketType() {
		return MateCommandFeedbackPacketType.MATE_COMMAND_SUCCESS;
	}

	/**
	 * Should be serialized as "command" (as a String)
	 * @return The {@link MateCommand} that was successfully sent to the MATE
	 */
	@JsonProperty("command")
	MateCommand getCommand();

	/**
	 * Should be serialized as "source"
	 *
	 * @return The string representing the source of the command
	 */
	@JsonProperty("source")
	String getSource();
}
