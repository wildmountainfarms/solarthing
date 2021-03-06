package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonDeserialize(as = ImmutableRequestCommandPacket.class)
@JsonTypeName("REQUEST_COMMAND")
@JsonExplicit
public interface RequestCommandPacket extends CommandOpenPacket {
	@DefaultFinal
	@Override
	default @NotNull CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.REQUEST_COMMAND;
	}

	@JsonProperty("commandName")
	@NotNull String getCommandName();
}
