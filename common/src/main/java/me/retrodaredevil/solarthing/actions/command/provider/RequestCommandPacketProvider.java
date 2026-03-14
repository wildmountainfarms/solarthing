package me.retrodaredevil.solarthing.actions.command.provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@JsonTypeName("requestcommand")
@NullMarked
public class RequestCommandPacketProvider implements CommandOpenProvider {
	private final String commandName;

	@JsonCreator
	public RequestCommandPacketProvider(
			@JsonProperty("command") @Nullable String commandName) {
		this.commandName = requireNonNull(commandName);
	}

	@Override
	public CommandOpenPacket get() {
		return new ImmutableRequestCommandPacket(commandName);
	}
}
