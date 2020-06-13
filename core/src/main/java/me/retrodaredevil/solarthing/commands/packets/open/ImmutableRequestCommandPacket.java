package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

public class ImmutableRequestCommandPacket implements RequestCommandPacket {
	private final String commandName;

	@JsonCreator
	public ImmutableRequestCommandPacket(
			@JsonProperty(value = "commandName", required = true) String commandName
	) {
		this.commandName = commandName;
	}

	@Override
	public @NotNull String getCommandName() {
		return commandName;
	}
}
