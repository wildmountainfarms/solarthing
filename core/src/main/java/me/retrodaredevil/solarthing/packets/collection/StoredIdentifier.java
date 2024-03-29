package me.retrodaredevil.solarthing.packets.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbStoredIdentifier;
import org.jetbrains.annotations.NotNull;

/**
 * StoredIdentifiers represent some unique identifier that is stored in the database along with
 * its timestamp.
 * <p>
 * Many times a particular identifier can be considered the same as another identifier based on all its attributes
 * except for its dateMillis attribute. This means that some implementations may not even consider dateMillis during equality or its hash code.
 */
@JsonSubTypes({
		@JsonSubTypes.Type(CouchDbStoredIdentifier.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class StoredIdentifier implements Comparable<StoredIdentifier> {
	/*
	Although these can be serialized to JSON, there is no particular use for serializing them to JSON *yet*.
	When I first added the ability to serialize CouchDbStoredIdentifier to JSON, I didn't realize how much
	that would tightly couple to CouchDB had I decided to put that in solarthing or solarthing_events.

	I'm keeping the ability to serialize to JSON for now, but just know that unless something changes since I write
	this comment, we could consider removing some JSON annotations around this code.
	 */
	protected final long dateMillis;

	protected StoredIdentifier(long dateMillis) {
		this.dateMillis = dateMillis;
	}

	@JsonProperty("dateMillis")
	public long getDateMillis() {
		return dateMillis;
	}

	@Override
	public abstract boolean equals(Object o);
	@Override
	public abstract int hashCode();
	@Override
	public abstract String toString();

	@Override
	public int compareTo(@NotNull StoredIdentifier storedIdentifier) {
		return Long.compare(dateMillis, storedIdentifier.dateMillis);
	}
}
