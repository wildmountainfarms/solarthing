package me.retrodaredevil.solarthing.actions.command.provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;

import static java.util.Objects.requireNonNull;

@JsonTypeName("requestcommand")
public class RequestCommandPacketProvider implements CommandOpenProvider {
	private final String commandName;

	@JsonCreator
	public RequestCommandPacketProvider(
			@JsonProperty("command") String commandName) {
		requireNonNull(this.commandName = commandName);
	}

	@Override
	public @NotNull CommandOpenPacket get() {
		return new ImmutableRequestCommandPacket(commandName);
	}
}
