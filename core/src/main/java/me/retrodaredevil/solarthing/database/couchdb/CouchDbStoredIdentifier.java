package me.retrodaredevil.solarthing.database.couchdb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.packets.collection.StoredIdentifier;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 *
 */
@JsonTypeName("couchdb")
public final class CouchDbStoredIdentifier extends StoredIdentifier {
	private final String id;
	private final String revision;

	@JsonCreator
	public CouchDbStoredIdentifier(
			@JsonProperty(value = "dateMillis", required = true) long dateMillis,
			@JsonProperty(value = "id", required = true) String id,
			@JsonProperty(value = "revision", required = true) String revision) {
		super(dateMillis);
		requireNonNull(this.id = id);
		requireNonNull(this.revision = revision);
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("revision")
	public String getRevision() {
		return revision;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CouchDbStoredIdentifier that = (CouchDbStoredIdentifier) o;
		return id.equals(that.id) && revision.equals(that.revision);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, revision);
	}

	@Override
	public String toString() {
		return "CouchDbStoredIdentifier(" +
				"dateMillis=" + dateMillis +
				", id='" + id + '\'' +
				", revision='" + revision + '\'' +
				')';
	}
}
