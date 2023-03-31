package me.retrodaredevil.solarthing.rest.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdb.design.MutablePacketsDesign;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUpdateConflictException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
@EnableScheduling
@EnableAsync
public class InMemoryDatabaseManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabaseManager.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final CouchDbDatabaseSettings couchDbDatabaseSettings;
	private final CouchDbDatabaseSettings replicateCouchDbDatabaseSettings;

	private volatile boolean isKnownGoodState = false;

	public InMemoryDatabaseManager(
			CouchDbDatabaseSettings couchDbDatabaseSettings,
			CouchDbDatabaseSettings replicateCouchDbDatabaseSettings) {
		this.couchDbDatabaseSettings = requireNonNull(couchDbDatabaseSettings);
		this.replicateCouchDbDatabaseSettings = replicateCouchDbDatabaseSettings;
	}

	@Scheduled(fixedDelayString = "PT20S")
	@Async
	public void setupInMemoryDatabase() throws CouchDbException {
		if (replicateCouchDbDatabaseSettings != null) {
			// if replicateCouchDbDatabaseSettings is not null, then we assume that couchDbDatabaseSettings refers to
			//   a database that is stored in memory. As such, we need to make sure it is fully setup and is able to replicate to replicateCouchDbDatabaseSettings
			// Unlike configuring a normal CouchDB instance, we are not going to configure any non-admin users. We just assume the credentials provided are admin.
			CouchDbInstance inMemoryInstance = CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties());
			for (SolarThingDatabaseType databaseType : new SolarThingDatabaseType[]{
					SolarThingDatabaseType.STATUS,
					SolarThingDatabaseType.EVENT,
					// no open
					SolarThingDatabaseType.CLOSED,
					// no alter
					SolarThingDatabaseType.CACHE,
			}) {
				CouchDbDatabase database = inMemoryInstance.getDatabase(databaseType.getName());
				if (database.createIfNotExists() || !isKnownGoodState) { // if just created or if we are maybe not in a good state
					MutablePacketsDesign design = new MutablePacketsDesign();
					if (databaseType.needsMillisView()) {
						design.addMillisNullView();
					}
					// don't check databaseType.needsSimpleAllDocsView() because that's only needed for alter database

					final JsonData jsonData;
					try {
						jsonData = new StringJsonData(MAPPER.writeValueAsString(design));
					} catch (JsonProcessingException e) {
						throw new RuntimeException("Couldn't serialize json! Report this!", e);
					}
					try {
						database.putDocument("_design/packets", jsonData);
					} catch (CouchDbUpdateConflictException e) {
						String revision = database.getCurrentRevision("_design/packets");
						database.updateDocument("_design/packets", revision, jsonData);
					}
					LOGGER.debug("Successfully updated database: " + database.getName());
				}
			}
			isKnownGoodState = true;
		}
	}
}
