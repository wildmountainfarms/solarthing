package me.retrodaredevil.solarthing.config.databases;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.CommonConfigUtil;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public final class DatabaseConfig {
	private final @Nullable DatabaseSettings databaseSettings;
	private final @Nullable DatabaseUsageSettings databaseUsageSettings;
	private final @Nullable Path externalDatabaseConfig;


	@JsonCreator
	public DatabaseConfig(
			@JsonProperty(value = "config")
			@JsonTypeInfo(
					use = JsonTypeInfo.Id.NAME,
					include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
					property = "type"
			)
			@Nullable DatabaseSettings databaseSettings,
			@JsonProperty("settings") DatabaseUsageSettings databaseUsageSettings,
			@JsonProperty("external") @Nullable Path externalDatabaseConfig) {
		if ((databaseSettings == null) == (externalDatabaseConfig == null)) {
			throw new IllegalArgumentException("Must only define config or external! Not both and not neither!");
		}
		this.databaseSettings = databaseSettings;
		this.databaseUsageSettings = databaseUsageSettings != null
				? (externalDatabaseConfig == null ? databaseUsageSettings.inheritFrom(DatabaseUsageSettings.DEFAULT) : databaseUsageSettings)
				: (externalDatabaseConfig == null ? DatabaseUsageSettings.DEFAULT : null);
		this.externalDatabaseConfig = externalDatabaseConfig;

	}
	public static DatabaseConfig fromExternal(Path externalDatabaseConfig) {
		return new DatabaseConfig(null, null, externalDatabaseConfig);
	}

	/**
	 * @return The {@link DatabaseType}. This determines what type {@link #requireDatabaseSettings()} will return
	 */
	public DatabaseType getType() {
		return requireDatabaseSettings().getDatabaseType();
	}

	public DatabaseSettings requireDatabaseSettings() {
		return requireNonNull(databaseSettings, "You must resolve the external configuration first!");
	}

	public DatabaseUsageSettings requireDatabaseUsageSettings() {
		return requireNonNull(databaseUsageSettings, "You must resolve the external configuration first!");
	}

	@Override
	public String toString() {
		return "DatabaseConfig(" +
				", settings=" + databaseSettings +
				", databaseUsageSettings=" + databaseUsageSettings +
				')';
	}

	public DatabaseConfig resolveExternal(ObjectMapper mapper) {
		if (externalDatabaseConfig == null) {
			// remember that if externalDatabaseConfig is null, then databaseUsageSettings is not null not matter what
			assert databaseUsageSettings != null;
			return this;
		}
		DatabaseConfig external = CommonConfigUtil.readConfig(externalDatabaseConfig, DatabaseConfig.class, mapper)
				.resolveExternal(mapper);
		// We have resolved the external DatabaseConfig, so external.databaseUsageSettings must not be null
		assert external.databaseUsageSettings != null;
		final DatabaseUsageSettings databaseUsageSettings;
		if (this.databaseUsageSettings == null) {
			databaseUsageSettings = external.databaseUsageSettings;
		} else {
			databaseUsageSettings = this.databaseUsageSettings.inheritFrom(external.databaseUsageSettings);
		}
		return new DatabaseConfig(
				external.databaseSettings,
				databaseUsageSettings,
				null
		);
	}
}
