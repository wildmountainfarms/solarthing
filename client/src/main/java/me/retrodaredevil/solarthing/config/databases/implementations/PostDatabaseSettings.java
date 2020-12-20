package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;

@JsonTypeName("post")
public final class PostDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("post");

	private final String url;

	@JsonCreator
	public PostDatabaseSettings(@JsonProperty(value = "url", required = true) String url) {
		this.url = url;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}

	public String getUrl() {
		return url;
	}
}
