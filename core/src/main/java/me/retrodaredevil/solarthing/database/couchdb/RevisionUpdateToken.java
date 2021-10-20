package me.retrodaredevil.solarthing.database.couchdb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.database.UpdateToken;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonExplicit
@JsonTypeName("couchdb-revision")
public class RevisionUpdateToken implements UpdateToken {
	private final String revision;

	@JsonCreator
	public RevisionUpdateToken(@JsonProperty(value = "revision", required = true) String revision) {
		requireNonNull(this.revision = revision);
	}

	@JsonProperty("revision")
	public String getRevision() {
		return revision;
	}

	@Override
	public String toString() {
		return "RevisionUpdateToken(" +
				"revision='" + revision + '\'' +
				')';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RevisionUpdateToken that = (RevisionUpdateToken) o;
		return revision.equals(that.revision);
	}

	@Override
	public int hashCode() {
		return Objects.hash(revision);
	}
}
