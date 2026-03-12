package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ImmutableRequestCommandPacket implements RequestCommandPacket {
	private final String commandName;

	@JsonCreator
	public ImmutableRequestCommandPacket(
			@JsonProperty(value = "commandName", required = true) String commandName
	) {
		this.commandName = requireNonNull(commandName);
	}

	@Override
	public String getCommandName() {
		return commandName;
	}

	@Override
	public String getUniqueString() {
		return "RequestCommandPacket(commandName=" + commandName + ")";
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImmutableRequestCommandPacket that = (ImmutableRequestCommandPacket) o;
		return commandName.equals(that.commandName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(commandName);
	}
}
