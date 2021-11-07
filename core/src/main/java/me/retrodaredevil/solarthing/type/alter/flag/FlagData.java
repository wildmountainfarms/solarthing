package me.retrodaredevil.solarthing.type.alter.flag;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Represents a flag's name and the period it is active during. A flag's name should not have whitespace in it, and should consist of ascii characters.
 * Although there is no strict enforcement of what the flag's name should be, it is recommended to make it something that could be a valid variable name.
 */
@JsonExplicit
public final class FlagData implements UniqueStringRepresentation {
	private final String flagName;
	private final ActivePeriod activePeriod;

	@JsonCreator
	public FlagData(
			@JsonProperty(value = "flagName", required = true) String flagName,
			@JsonProperty(value = "activePeriod", required = true) ActivePeriod activePeriod) {
		requireNonNull(this.flagName = flagName);
		requireNonNull(this.activePeriod = activePeriod);
	}

	@JsonProperty("flagName")
	public String getFlagName() {
		return flagName;
	}

	@JsonProperty("activePeriod")
	public ActivePeriod getActivePeriod() {
		return activePeriod;
	}

	@Override
	public @NotNull String getUniqueString() {
		return "FlagData(" +
				"flagName='" + flagName + '\'' +
				", activePeriod=" + activePeriod.getUniqueString() +
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
		FlagData flagData = (FlagData) o;
		return flagName.equals(flagData.flagName) && activePeriod.equals(flagData.activePeriod);
	}

	@Override
	public int hashCode() {
		return Objects.hash(flagName, activePeriod);
	}
}
