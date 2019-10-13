package me.retrodaredevil.solarthing.config.databases;

import me.retrodaredevil.solarthing.packets.handling.FrequencySettings;

public final class IndividualSettings {
	private final FrequencySettings frequencySettings;
	
	public IndividualSettings(FrequencySettings frequencySettings) {
		this.frequencySettings = frequencySettings;
	}
	
	public FrequencySettings getFrequencySettings() {
		return frequencySettings;
	}
}
