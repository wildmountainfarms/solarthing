package me.retrodaredevil.solarthing.config.databases;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.LatestFileDatabaseSettings;

@JsonSubTypes({
		@JsonSubTypes.Type(CouchDbDatabaseSettings.class),
		@JsonSubTypes.Type(InfluxDbDatabaseSettings.class),
		@JsonSubTypes.Type(LatestFileDatabaseSettings.class)
})
public interface DatabaseSettings {
	DatabaseType getDatabaseType();
}
