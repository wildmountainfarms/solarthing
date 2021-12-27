package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.database.cache.DatabaseCache;
import me.retrodaredevil.solarthing.util.sync.ResourceManager;

public class EventDatabaseCacheEnvironment {
	private final ResourceManager<? extends DatabaseCache> eventDatabaseCacheManager;

	public EventDatabaseCacheEnvironment(ResourceManager<? extends DatabaseCache> eventDatabaseCacheManager) {
		this.eventDatabaseCacheManager = eventDatabaseCacheManager;
	}

	public ResourceManager<? extends DatabaseCache> getEventDatabaseCacheManager() {
		return eventDatabaseCacheManager;
	}
}
