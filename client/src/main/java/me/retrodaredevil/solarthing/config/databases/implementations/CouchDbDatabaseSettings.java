package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;

@JsonTypeName("couchdb")
@JsonExplicit
public final class CouchDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("couchdb");

	@JsonUnwrapped
	private final CouchProperties couchProperties;
	
	public CouchDbDatabaseSettings(CouchProperties couchProperties) {
		this.couchProperties = couchProperties;
	}
	
	public CouchProperties getCouchProperties() {
		return couchProperties;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}
}
