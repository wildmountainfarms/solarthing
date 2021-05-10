package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.solarthing.database.UpdateToken;

import static java.util.Objects.requireNonNull;

public class RevisionUpdateToken implements UpdateToken {
	private final String revision;

	public RevisionUpdateToken(String revision) {
		requireNonNull(this.revision = revision);
	}

	public String getRevision() {
		return revision;
	}

	@Override
	public String toString() {
		return "RevisionUpdateToken(" +
				"revision='" + revision + '\'' +
				')';
	}
}
