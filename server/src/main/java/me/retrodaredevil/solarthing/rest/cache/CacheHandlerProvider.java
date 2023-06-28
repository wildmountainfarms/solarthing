package me.retrodaredevil.solarthing.rest.cache;

import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class CacheHandlerProvider {

	private final DefaultInstanceOptions defaultInstanceOptions;
	private final CouchDbDatabaseSettings couchDbDatabaseSettings;

	private CacheHandler cacheHandler;

	public CacheHandlerProvider(DefaultInstanceOptions defaultInstanceOptions, CouchDbDatabaseSettings couchDbDatabaseSettings) {
		this.defaultInstanceOptions = defaultInstanceOptions;
		this.couchDbDatabaseSettings = couchDbDatabaseSettings;
	}

	@PostConstruct
	public void init() {
		CouchDbInstance instance = CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties());
		cacheHandler = new CacheHandler(JacksonUtil.defaultMapper(), defaultInstanceOptions, instance);

	}
	@Bean
	public CacheHandler cacheHandler() {
		return cacheHandler;
	}
}
