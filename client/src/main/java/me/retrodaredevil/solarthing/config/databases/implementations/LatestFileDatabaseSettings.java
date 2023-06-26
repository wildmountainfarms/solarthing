package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;

import java.nio.file.Path;

@JsonTypeName("latest")
public final class LatestFileDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("latest");

	private final Path file;

	@JsonCreator
	public LatestFileDatabaseSettings(@JsonProperty(value = "file", required = true) Path file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "Latest " + file;
	}

	public Path getFile(){
		return file;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}
}
