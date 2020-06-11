package me.retrodaredevil.solarthing.commands.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.commands.CommandInfo;

import java.util.List;

@JsonDeserialize(as = ImmutableAvailableCommandsPacket.class)
@JsonTypeName("AVAILABLE_COMMANDS")
@JsonExplicit
public interface AvailableCommandsPacket extends CommandStatusPacket {
	@Override
	default @NotNull CommandStatusPacketType getPacketType() {
		return CommandStatusPacketType.AVAILABLE_COMMANDS;
	}

	@JsonProperty("commands")
	List<CommandInfo> getCommandInfoList();

}
