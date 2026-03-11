package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class ImmutableRequestFlagPacket implements RequestFlagPacket {
	private final FlagData flagData;

	@JsonCreator
	public ImmutableRequestFlagPacket(
			@JsonProperty("flagData") FlagData flagData) {
		this.flagData = flagData;
	}

	@Override
	public @NonNull FlagData getFlagData() {
		return flagData;
	}

	@Override
	public @NonNull String getUniqueString() {
		return "RequestFlagPacket(" +
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
