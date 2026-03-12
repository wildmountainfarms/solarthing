package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;

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
