package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.config.databases.DatabaseConfig;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public class DatabaseConfigSettings {

	public static final DatabaseConfigSettings EMPTY = new DatabaseConfigSettings(Collections.emptyList());

	private final List<DatabaseConfig> databases;

	@JsonCreator
	public DatabaseConfigSettings(@JsonProperty("databases") @Nullable List<DatabaseConfig> databases) {
		this.databases = databases == null ? Collections.emptyList() : List.copyOf(databases);
	}

	public List<DatabaseConfig> getDatabases() {
		return databases;
	}
	public List<DatabaseConfig> resolveConfigs(ObjectMapper mapper) {
		return databases.stream().map(databaseConfig -> databaseConfig.resolveExternal(mapper)).collect(Collectors.toList());
	}
}
