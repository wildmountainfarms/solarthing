package me.retrodaredevil.solarthing.type.alter.flag;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public final class FlagAliasData implements UniqueStringRepresentation {
	private final String flagName;
	private final String flagAlias;
	private final Long defaultDurationMillis;
	// For the time being, a FlagAliasData only stores a duration for how long the flag should stay active.
	//   In the future, we may consider adding a more future-proof way to represent the desired ActivePeriod

	@JsonCreator
	public FlagAliasData(
			@JsonProperty(value = "flagName", required = true) String flagName,
			@JsonProperty(value = "flagAlias", required = true) String flagAlias,
			@JsonProperty(value = "defaultDurationMillis", required = true) Long defaultDurationMillis) {
		this.flagName = requireNonNull(flagName);
		this.flagAlias = requireNonNull(flagAlias);
		this.defaultDurationMillis = defaultDurationMillis;
	}

	/**
	 * @return The flag name
	 */
	@JsonProperty("flagName")
	public @NonNull String getFlagName() {
		return flagName;
	}

	/**
	 * @return The flag alias or display name
	 */
	@JsonProperty("flagAlias")
	public @NonNull String getFlagAlias() {
		return flagAlias;
	}

	/**
	 * @return The default duration for the flag to set for in milliseconds.
	 */
	@JsonProperty("defaultDurationMillis")
	public @Nullable Long getDefaultDurationMillis() {
		return defaultDurationMillis;
	}
	/**
	 * @return The default duration for the flag to set for.
	 */
	public @Nullable Duration getDefaultDuration() {
		return defaultDurationMillis == null ? null : Duration.ofMillis(defaultDurationMillis);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FlagAliasData that = (FlagAliasData) o;
		return flagName.equals(that.flagName) && flagAlias.equals(that.flagAlias) && Objects.equals(defaultDurationMillis, that.defaultDurationMillis);
	}

	@Override
	public int hashCode() {
		return Objects.hash(flagName, flagAlias, defaultDurationMillis);
	}

	@Override
	public @NonNull String getUniqueString() {
		return "FlagAliasData(" +
				"flagName='" + flagName + '\'' +
				", flagAlias=" + flagAlias +
				')';

	}

	@Override
	public String toString() {
		return getUniqueString();
	}
}
