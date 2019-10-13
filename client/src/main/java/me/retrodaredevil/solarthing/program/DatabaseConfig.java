package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.packets.handling.FrequencySettings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DatabaseConfig {
	private final DatabaseType type;
	private final DatabaseSettings settings;
	private final Map<String, IndividualSettings> individualSettingsMap;
	
	public DatabaseConfig(DatabaseType type, DatabaseSettings settings, Map<String, IndividualSettings> frequencySettingsMap) {
		this.type = type;
		this.settings = settings;
		this.individualSettingsMap = Collections.unmodifiableMap(new HashMap<>(frequencySettingsMap));
	}

	/**
	 * @return The {@link DatabaseType}. This determines what type {@link #getSettings()} will return
	 */
	public DatabaseType getType() {
		return type;
	}
	
	public DatabaseSettings getSettings() {
		return settings;
	}
	
	public Map<String, IndividualSettings> getIndividualSettingsMap() {
		return individualSettingsMap;
	}
	public IndividualSettings getIndividualSettingsOrDefault(String key, IndividualSettings defaultSettings){
		IndividualSettings r = individualSettingsMap.get(key);
		if(r == null){
			r = defaultSettings;
		}
		return r;
	}
}
