package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.database.cache.DatabaseCache;
import me.retrodaredevil.solarthing.util.sync.ResourceManager;

/**
 * Provides access to the resource manager for the status database.
 *
 * Note: You should only use this if you need historical data. For the most recent data, use a {@link me.retrodaredevil.solarthing.FragmentedPacketGroupProvider}
 */
public class StatusDatabaseCacheEnvironment {
	private final ResourceManager<? extends DatabaseCache> statusDatabaseCacheManager;

	public StatusDatabaseCacheEnvironment(ResourceManager<? extends DatabaseCache> statusDatabaseCacheManager) {
		this.statusDatabaseCacheManager = statusDatabaseCacheManager;
	}

	public ResourceManager<? extends DatabaseCache> getStatusDatabaseCacheManager() {
		return statusDatabaseCacheManager;
	}
}
