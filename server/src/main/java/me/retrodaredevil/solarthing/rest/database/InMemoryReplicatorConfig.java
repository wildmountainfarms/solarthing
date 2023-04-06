package me.retrodaredevil.solarthing.rest.database;

import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.design.Design;
import me.retrodaredevil.couchdb.design.DesignResource;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.replicator.ReplicatorDocument;
import me.retrodaredevil.couchdbjava.replicator.SimpleReplicatorDocument;
import me.retrodaredevil.couchdbjava.replicator.source.ObjectReplicatorSource;
import me.retrodaredevil.couchdbjava.replicator.source.ReplicatorSource;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import static java.util.Objects.requireNonNull;

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
	 * @return This duration represents how far back packets should be retrieved from
	 */
	public @Nullable Duration getDuplicatePastDuration() {
		return duplicatePastDuration;
	}

	public String getDocumentId() {
		return externalIsTarget
				? "this_" + databaseType.getName() + "into_external_" + databaseType.getName()
				: "external_" + databaseType.getName() + "_into_this_" + databaseType.getName();
	}

	public ReplicatorDocument createDocument(CouchProperties inMemoryCouch, CouchProperties externalCouch) {
		ReplicatorSource inMemoryDatabase = createSource(inMemoryCouch, databaseType.getName());
		ReplicatorSource externalDatabase = createSource(externalCouch, databaseType.getName());
		SimpleReplicatorDocument.Builder builder = SimpleReplicatorDocument.builder(
						externalIsTarget ? inMemoryDatabase : externalDatabase,
						externalIsTarget ? externalDatabase : inMemoryDatabase
				)
				.continuous();

		return builder.build();
	}
	public @Nullable Design createFilterDesignDocument() {
		if (duplicatePastDuration != null) {
			return new RecentPacketsFilterDesign();
		}
		return null;
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

	class RecentPacketsFilterDesign implements Design {

		private final String recentPacketsFilter;

		public RecentPacketsFilterDesign() {
			requireNonNull(duplicatePastDuration);
			String rawFunction = DesignResource.FILTER_JAVASCRIPT_RECENT_PACKETS.getAsString(StandardCharsets.UTF_8);
			recentPacketsFilter = rawFunction.replaceAll("\\$\\$durationMillis\\$\\$", Long.toString(duplicatePastDuration.toMillis()));
		}

		@Override
		public String getLanguage() {
			return "javascript";
		}

		@Override
		public Map<String, String> getFilters() {
			return Map.of(
					"recent", recentPacketsFilter
			);
		}
	}
}
