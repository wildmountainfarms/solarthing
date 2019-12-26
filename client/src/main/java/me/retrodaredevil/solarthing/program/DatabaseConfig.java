package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonExplicit
public final class DatabaseConfig {
	@JsonProperty(value = "type", required = true)
	private final String type;
	@JsonProperty(value = "config", required = true)
	@JsonTypeInfo(
			use = JsonTypeInfo.Id.NAME,
			include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
			property = "type"
	)
	private final DatabaseSettings settings;
	@JsonProperty("settings")
	private final Map<String, IndividualSettings> individualSettingsMap;

	private DatabaseConfig(){
		// Jackson will call this constructor and then serialize fields
		type = null;
		settings = null;
		individualSettingsMap = Collections.emptyMap();
	}
	
	public DatabaseConfig(DatabaseSettings settings, Map<String, IndividualSettings> frequencySettingsMap) {
		this.settings = settings;
		this.individualSettingsMap = Collections.unmodifiableMap(new HashMap<>(frequencySettingsMap));
		type = settings.getDatabaseType().getName();
	}

	/**
	 * @return The {@link DatabaseType}. This determines what type {@link #getSettings()} will return
	 */
	public DatabaseType getType() {
		return settings.getDatabaseType();
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
