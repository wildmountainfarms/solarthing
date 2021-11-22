package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;

public class CouchDbEnvironment {
	private final CouchDbDatabaseSettings databaseSettings;

	public CouchDbEnvironment(CouchDbDatabaseSettings databaseSettings) {
		this.databaseSettings = databaseSettings;
	}

	public CouchProperties getCouchProperties() {
		return databaseSettings.getCouchProperties();
	}
	public OkHttpProperties getOkHttpProperties() {
		return databaseSettings.getOkHttpProperties();
	}
	public CouchDbDatabaseSettings getDatabaseSettings() {
		return databaseSettings;
	}

}
