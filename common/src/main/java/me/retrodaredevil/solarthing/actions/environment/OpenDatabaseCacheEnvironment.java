package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.database.cache.DatabaseCache;

public class OpenDatabaseCacheEnvironment {
	private final DatabaseCache openDatabaseCache;

	public OpenDatabaseCacheEnvironment(DatabaseCache openDatabaseCache) {
		this.openDatabaseCache = openDatabaseCache;
	}

	public DatabaseCache getOpenDatabaseCache() {
		return openDatabaseCache;
	}
}
