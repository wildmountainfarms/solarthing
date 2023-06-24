package me.retrodaredevil.solarthing.rest.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.couchdb.design.MutablePacketsDesign;
import me.retrodaredevil.couchdbjava.CouchDbConfig;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbCodeException;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUpdateConflictException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.replicator.ReplicatorDocument;
import me.retrodaredevil.couchdbjava.replicator.SimpleReplicatorDocument;
import me.retrodaredevil.couchdbjava.request.ViewQuery;
import me.retrodaredevil.couchdbjava.request.ViewQueryParams;
import me.retrodaredevil.couchdbjava.request.ViewQueryParamsBuilder;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Component
@EnableScheduling
@EnableAsync
public class InMemoryDatabaseManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabaseManager.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final CouchDbDatabaseSettings couchDbDatabaseSettings;
	private final CouchDbDatabaseSettings replicateCouchDbDatabaseSettings;

	private volatile boolean areDatabasesKnownToBeFullySetup = false;
	private volatile boolean isReplicatorKnownToBeFullySetup = false;

	public InMemoryDatabaseManager(
			CouchDbDatabaseSettings couchDbDatabaseSettings,
			ObjectProvider<CouchDbDatabaseSettings> replicateCouchDbDatabaseSettings) {
		// we must use an ObjectProvider because a null value is valid, but Spring handles null beans as not being present: https://stackoverflow.com/questions/49044770/change-in-how-spring-5-handles-null-beans
		this.couchDbDatabaseSettings = requireNonNull(couchDbDatabaseSettings);
		this.replicateCouchDbDatabaseSettings = replicateCouchDbDatabaseSettings.getIfAvailable();
	}

	private void setupDatabase(CouchDbInstance inMemoryInstance) throws CouchDbException {
		for (SolarThingDatabaseType databaseType : new SolarThingDatabaseType[]{
				SolarThingDatabaseType.STATUS,
				SolarThingDatabaseType.EVENT,
				// no open
				SolarThingDatabaseType.CLOSED,
				// no alter
				SolarThingDatabaseType.CACHE,
		}) {
			CouchDbDatabase database = inMemoryInstance.getDatabase(databaseType.getName());
			if (database.createIfNotExists() || !areDatabasesKnownToBeFullySetup) { // if just created or if we are maybe not in a good state
				areDatabasesKnownToBeFullySetup = false; // if we just created a database, then we aren't certain of the state of the databases. We also don't care if we set false to false
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
					// must get getDocument() and not getCurrentRevision() because PouchDB does not support getCurrentRevision()
					JsonData existingJsonData = database.getDocument("_design/packets").getJsonData();
					final JsonNode jsonNode;
					try {
						jsonNode = CouchDbJacksonUtil.getNodeFrom(existingJsonData);
					} catch (JsonProcessingException ex) {
						throw new RuntimeException("The database must have given us bad JSON data", ex);
					}
					String revision = jsonNode.get("_rev").asText(); // we assume jsonNode is an object and that the _rev property exists
					database.updateDocument("_design/packets", revision, jsonData);
				}
				LOGGER.debug("Successfully updated database: " + database.getName());
			} else {
				// If we are in this else block, that means that we know database already exists and that we created all the databases before.
				//   With this information we can assume that all the other databases are still fully setup, and can stop early.
				break;
			}
		}
		areDatabasesKnownToBeFullySetup = true;
	}

	/**
	 * @param inMemoryInstance The in memory database to set up replication on. This method assumes it has authorization applied to it
	 * @param inMemoryCouch The {@link CouchProperties} of {@code inMemoryInstance}
	 * @param externalInstance The external database. This method assumes it has authorization applied to it
	 * @param externalCouch The {@link CouchDbDatabaseSettings} of the target database
	 */
	private void setupReplicator(CouchDbInstance inMemoryInstance, CouchProperties inMemoryCouch, CouchDbInstance externalInstance, CouchProperties externalCouch) throws CouchDbException {
		CouchDbDatabase replicator = inMemoryInstance.getReplicatorDatabase();
		replicator.createIfNotExists(); // On the PouchDB instance I've interacted with this database is already present, but let's just make sure

		CouchDbConfig config = inMemoryInstance.getConfig(); // use root _config // this only works on PouchDB
		config.test();
		String portString = config.queryValue("httpd", "port"); // on CouchDB the section is chttpd, but here it is httpd
		int inMemoryPort = Integer.parseInt(portString);
		// We could use the inMemoryCouch that we use to refer to it, but it's much better to have a localhost address
		CouchProperties localInMemoryCouchProperties = new CouchPropertiesBuilder()
				.setHttpUrl(new HttpUrl.Builder()
						.scheme("http").host("localhost").port(inMemoryPort).build())
				.setUsername(inMemoryCouch.getUsername())
				.setPassword(inMemoryCouch.getPassword())
				.build();

		boolean haveWeConfirmedThatReplicatorIsProbablySetupThisIteration = false;
		for (InMemoryReplicatorConfig replicatorConfig : InMemoryReplicatorConfig.values()) {
			SimpleReplicatorDocument.Builder replicatorDocumentBuilder = replicatorConfig.createDocumentBuilder(localInMemoryCouchProperties, externalCouch);
			Duration duplicatePastDuration = replicatorConfig.getDuplicatePastDuration();
			if (duplicatePastDuration != null) {
				// replicating solarthing and solarthing_events to in memory database
				// In this case we need to stop PouchDB from replicating all the documents.
				// Ideally we would be able to just use descending=true with some sort of limit, but PouchDB doesn't support that in its replication
				//   The other option would be to use a filter, but typical filters defined in design documents require processing ALL documents in a database
				// So, this is the solution, and you have to trust me that it is the best last resort solution
				CouchDbDatabase database = externalInstance.getDatabase(replicatorConfig.getDatabaseType().getName());
				Instant instant24HoursAgo = Instant.now().minus(duplicatePastDuration);
				ViewQueryParams params = new ViewQueryParamsBuilder()
						.descending() // get most recent first
						.endKey(instant24HoursAgo.toEpochMilli())
						.build();
				ViewQuery millisNullLast24HoursViewQuery = SolarThingCouchDb.createMillisNullView(params);
				ViewResponse viewResponse = database.queryView(millisNullLast24HoursViewQuery);
				List<String> documentIds = viewResponse.getRows().stream().map(ViewResponse.DocumentEntry::getId).toList();
				replicatorDocumentBuilder
						.filter("_doc_ids")
						.documentIds(documentIds);
				LOGGER.info("Using " + documentIds.size() + " document IDs as a restraint for replication config: " + replicatorConfig);
			}
			if (!haveWeConfirmedThatReplicatorIsProbablySetupThisIteration || duplicatePastDuration != null) {
				ReplicatorDocument replicatorDocument = replicatorDocumentBuilder.build();
				String documentId = replicatorConfig.getDocumentId();

				JsonData jsonData;
				try {
					jsonData = new StringJsonData(MAPPER.writeValueAsString(replicatorDocument));
				} catch (JsonProcessingException e) {
					throw new RuntimeException("This should not happen", e);
				}
				try {
					replicator.putDocument(documentId, jsonData);
					isReplicatorKnownToBeFullySetup = false; // if we successfully upload this document, then the replicator is not already fully setup
				} catch (CouchDbUpdateConflictException e) {
					if (isReplicatorKnownToBeFullySetup) {
						haveWeConfirmedThatReplicatorIsProbablySetupThisIteration = true;
						// If we have confirmed before that the replicator is set up, then we can be confident that it's still setup.
					}
					// do not use getCurrentRevision() because that will fail on PouchDB
					String revision = replicator.getDocument(documentId).getRevision(); // ask the database for the revision so we can overwrite it
					try {
						replicator.updateDocument(documentId, revision, jsonData);
					} catch (CouchDbCodeException updateException) {
						if (updateException.getCode() == 403) { //
							LOGGER.debug("(403) This replication document is likely triggered. documentId: " + documentId);
						} else {
							throw updateException;
						}
					}
				} catch (CouchDbCodeException e) {
					if (e.getCode() != 403) {
						throw e;
					}
					LOGGER.debug("(403) this replication document is likely triggered. documentId: " + documentId);
					if (isReplicatorKnownToBeFullySetup) {
						haveWeConfirmedThatReplicatorIsProbablySetupThisIteration = true;
					}
				}
			}
		}
		// Note that although we have isReplicatorKnownToBeFullySetup, we don't return in the above loop because we need to constantly duplicate packets from last 24 hours
		isReplicatorKnownToBeFullySetup = true;
	}

	@Scheduled(fixedDelayString = "PT20S")
	@Async
	public void setupInMemoryDatabase() throws CouchDbException {
		if (replicateCouchDbDatabaseSettings != null) {
			// if replicateCouchDbDatabaseSettings is not null, then we assume that couchDbDatabaseSettings refers to
			//   a database that is stored in memory. As such, we need to make sure it is fully setup and is able to replicate to replicateCouchDbDatabaseSettings
			// Unlike configuring a normal CouchDB instance, we are not going to configure any non-admin users. We just assume the credentials provided are admin.
			CouchProperties inMemoryCouch = couchDbDatabaseSettings.getCouchProperties();
			CouchProperties externalCouch = replicateCouchDbDatabaseSettings.getCouchProperties();
			CouchDbInstance inMemoryInstance = CouchDbUtil.createInstance(inMemoryCouch, couchDbDatabaseSettings.getOkHttpProperties());
			CouchDbInstance externalInstance = CouchDbUtil.createInstance(externalCouch, replicateCouchDbDatabaseSettings.getOkHttpProperties());
			setupDatabase(inMemoryInstance);
			setupReplicator(inMemoryInstance, inMemoryCouch, externalInstance, externalCouch);
		}
	}

}
