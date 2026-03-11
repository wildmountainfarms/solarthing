package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.type.alter.flag.FlagAliasData;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class ImmutableFlagAliasAddPacket implements FlagAliasAddPacket {

	private final FlagAliasData flagAliasData;

	@JsonCreator
	public ImmutableFlagAliasAddPacket(@JsonProperty(value = "flagAliasData", required = true) FlagAliasData flagAliasData) {
		this.flagAliasData = requireNonNull(flagAliasData);
	}

	@Override
	public @NonNull FlagAliasData getFlagAliasData() {
		return flagAliasData;
	}

	@Override
	public @NonNull String getUniqueString() {
		return "FlagAliasAdd(flagAliasData=" + flagAliasData + ")";
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImmutableFlagAliasAddPacket that = (ImmutableFlagAliasAddPacket) o;
		return flagAliasData.equals(that.flagAliasData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(flagAliasData);
	}
}
