package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ImmutableRequestCommandPacket.class)
@JsonTypeName("REQUEST_COMMAND")
@JsonExplicit
@NullMarked
public interface RequestCommandPacket extends CommandOpenPacket {
	@DefaultFinal
	@Override
	default CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.REQUEST_COMMAND;
	}

	@JsonProperty("commandName")
	String getCommandName();
}
