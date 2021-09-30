package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.solarthing.packets.collection.StoredIdentifier;

import java.util.Objects;

public final class CouchDbStoredIdentifier extends StoredIdentifier {
	private final String id;
	private final String revision;

	public CouchDbStoredIdentifier(long dateMillis, String id, String revision) {
		super(dateMillis);
		this.id = id;
		this.revision = revision;
	}

	public String getId() {
		return id;
	}

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
				"id='" + id + '\'' +
				", revision='" + revision + '\'' +
				')';
	}
}
