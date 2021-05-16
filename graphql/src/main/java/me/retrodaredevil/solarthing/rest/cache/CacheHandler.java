package me.retrodaredevil.solarthing.rest.cache;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.solarthing.SolarThingConstants;

public class CacheHandler {
	private final CouchDbInstance couchDbInstance;
	private final CouchDbDatabase cacheDatabase;

	public CacheHandler(CouchDbInstance couchDbInstance) {
		this.couchDbInstance = couchDbInstance;

		cacheDatabase = couchDbInstance.getDatabase(SolarThingConstants.CACHE_UNIQUE_NAME);
	}
}
