package me.retrodaredevil.solarthing.rest.cache;

import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CacheHandlerProvider {

	private final CouchDbDatabaseSettings couchDbDatabaseSettings;

	private CacheHandler cacheHandler;

	public CacheHandlerProvider(CouchDbDatabaseSettings couchDbDatabaseSettings) {
		this.couchDbDatabaseSettings = couchDbDatabaseSettings;
	}

	@PostConstruct
	public void init() {
		CouchDbInstance instance = CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties());
		cacheHandler = new CacheHandler(instance);

	}
	@Bean
	public CacheHandler cacheHandler() {
		return cacheHandler;
	}
}
