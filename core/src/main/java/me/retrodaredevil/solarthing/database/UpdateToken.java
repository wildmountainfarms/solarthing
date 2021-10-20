package me.retrodaredevil.solarthing.database;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.database.couchdb.RevisionUpdateToken;

@JsonSubTypes({
		@JsonSubTypes.Type(RevisionUpdateToken.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface UpdateToken {
}
