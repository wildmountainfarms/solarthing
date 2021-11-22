package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.database.cache.DatabaseCache;

public class EventDatabaseCacheEnvironment {
	private final DatabaseCache eventDatabaseCache;

	public EventDatabaseCacheEnvironment(DatabaseCache eventDatabaseCache) {
		this.eventDatabaseCache = eventDatabaseCache;
	}

	public DatabaseCache getEventDatabaseCache() {
		return eventDatabaseCache;
	}
}
