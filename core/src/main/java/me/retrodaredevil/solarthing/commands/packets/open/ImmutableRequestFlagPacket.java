package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;

import java.util.Objects;

public class ImmutableRequestFlagPacket implements RequestFlagPacket {
	private final FlagData flagData;

	@JsonCreator
	public ImmutableRequestFlagPacket(
			@JsonProperty("flagData") FlagData flagData) {
		this.flagData = flagData;
	}

	@Override
	public @NotNull FlagData getFlagData() {
		return flagData;
	}

	@Override
	public @NotNull String getUniqueString() {
		return "ImmutableRequestFlagPacket(" +
				"flagData=" + flagData.getUniqueString() +
				')';

	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImmutableRequestFlagPacket that = (ImmutableRequestFlagPacket) o;
		return flagData.equals(that.flagData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(flagData);
	}
}
