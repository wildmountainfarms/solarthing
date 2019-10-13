package me.retrodaredevil.solarthing.config.databases.implementations;

import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;

public final class CouchDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new DatabaseType() { };
	
	private final CouchProperties couchProperties;
	
	public CouchDbDatabaseSettings(CouchProperties couchProperties) {
		this.couchProperties = couchProperties;
	}
	
	public CouchProperties getCouchProperties() {
		return couchProperties;
	}
}
