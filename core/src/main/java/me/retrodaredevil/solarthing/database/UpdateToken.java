package me.retrodaredevil.solarthing.database;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.couchdb.RevisionUpdateToken;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.StoredIdentifier;

/**
 * Update tokens are an abstraction to allow the updating of already uploaded data. They are also useful for caches.
 * <p>
 * UpdateTokens can be serialized to JSON. As of right now, UpdateTokens should only be serialized and stored in the alter database.
 */
@JsonSubTypes({
		@JsonSubTypes.Type(RevisionUpdateToken.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface UpdateToken {
	@NotNull StoredIdentifier toStoredIdentifier(@NotNull PacketCollection packetCollection);
}
