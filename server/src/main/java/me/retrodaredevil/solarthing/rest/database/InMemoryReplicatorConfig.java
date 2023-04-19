package me.retrodaredevil.solarthing.rest.database;

import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.replicator.SimpleReplicatorDocument;
import me.retrodaredevil.couchdbjava.replicator.source.ObjectReplicatorSource;
import me.retrodaredevil.couchdbjava.replicator.source.ReplicatorSource;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.net.URI;
import java.time.Duration;


/**
 * Represents the replication configs needed when setting up an in memory database
 */
public enum InMemoryReplicatorConfig {
	STATUS_IN_MEMORY_TO_EXTERNAL(SolarThingDatabaseType.STATUS, true, null),
	STATUS_EXTERNAL_TO_IN_MEMORY(SolarThingDatabaseType.STATUS, false, Duration.ofHours(24)),

	EVENT_IN_MEMORY_TO_EXTERNAL(SolarThingDatabaseType.EVENT, true, null),
	EVENT_EXTERNAL_TO_IN_MEMORY(SolarThingDatabaseType.EVENT, false, Duration.ofHours(24)),

	CLOSED_EXTERNAL_TO_IN_MEMORY(SolarThingDatabaseType.CLOSED, false, null),
	;

	private final SolarThingDatabaseType databaseType;
	private final boolean externalIsTarget;
	private final @Nullable Duration duplicatePastDuration;

	InMemoryReplicatorConfig(SolarThingDatabaseType databaseType, boolean externalIsTarget, @Nullable Duration duplicatePastDuration) {
		this.databaseType = databaseType;
		this.externalIsTarget = externalIsTarget;
		this.duplicatePastDuration = duplicatePastDuration;
	}

	public SolarThingDatabaseType getDatabaseType() {
		return databaseType;
	}

	/**
	 * @return true if the source is the in memory database and the external database is the target
	 */
	public boolean isExternalIsTarget() {
		return externalIsTarget;
	}

	/**
	 * Note: Only supported on databases where all documents have a {@code dateMillis} field.
	 * <p>
	 * Note: At the time of writing, it is safe to assume that {@link #isExternalIsTarget()} is false if this returns a non-null value. (The in memory database is the target here)
	 * @return This duration represents how far back packets should be retrieved from
	 */
	public @Nullable Duration getDuplicatePastDuration() {
		return duplicatePastDuration;
	}

	public String getDocumentId() {
		return externalIsTarget
				? "this_" + databaseType.getName() + "_into_external_" + databaseType.getName()
				: "external_" + databaseType.getName() + "_into_this_" + databaseType.getName();
	}

	/**
	 * @param inMemoryCouch The {@link CouchProperties} the in memory database can use to refer to itself. {@link CouchProperties#getUri()} should be a localhost address.
	 * @param externalCouch The external {@link CouchProperties} the in memory database can use to refer to the external database.
	 * @return
	 */
	public SimpleReplicatorDocument.Builder createDocumentBuilder(CouchProperties inMemoryCouch, CouchProperties externalCouch) {
		ReplicatorSource inMemoryDatabase = createSource(inMemoryCouch, databaseType.getName());
		ReplicatorSource externalDatabase = createSource(externalCouch, databaseType.getName());

		return SimpleReplicatorDocument.builder(
						externalIsTarget ? inMemoryDatabase : externalDatabase,
						externalIsTarget ? externalDatabase : inMemoryDatabase
				)
				.continuous();
	}

	/**
	 * Creates a {@link ReplicatorSource} with the URL and authentication from the provided properties.
	 */
	private static ReplicatorSource createSource(CouchProperties couchProperties, String databaseName) {
		// TODO consider somehow moving this "URI building logic" into couchdbjava into a CouchDbDatabase method
		//   I'm not exactly sure how it would work since we probably would still need to provide the URI of the database.
		URI uri = couchProperties.getUri().resolve(databaseName);
		ObjectReplicatorSource.Builder builder = ObjectReplicatorSource.builder(uri);
		CouchDbAuth auth = couchProperties.getAuth();
		if (auth.usesAuth()) { // at the time of writing, we have to check this here as the call to addAuthHeader() does not make this check
			builder.addAuthHeader(auth);
		}
		return builder.build();
	}

}
