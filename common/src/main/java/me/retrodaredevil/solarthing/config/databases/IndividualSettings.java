package me.retrodaredevil.solarthing.config.databases;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.solarthing.packets.handling.FrequencySettings;

@Deprecated
public final class IndividualSettings {
	private final FrequencySettings frequencySettings;

	@JsonCreator
	public IndividualSettings(@JsonUnwrapped FrequencySettings frequencySettings) {
		this.frequencySettings = frequencySettings;
	}

	public FrequencySettings getFrequencySettings() {
		return frequencySettings;
	}
}
