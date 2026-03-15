package me.retrodaredevil.solarthing.config.databases;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.solarthing.packets.handling.FrequencySettings;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Deprecated
@NullMarked
public final class IndividualSettings {
	private final @Nullable FrequencySettings frequencySettings;

	@JsonCreator
	public IndividualSettings(@JsonUnwrapped @Nullable FrequencySettings frequencySettings) {
		this.frequencySettings = frequencySettings;
	}

	public @Nullable FrequencySettings getFrequencySettings() {
		return frequencySettings;
	}
}
