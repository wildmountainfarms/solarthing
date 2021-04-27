package me.retrodaredevil.couchdbjava.response;

import static java.util.Objects.requireNonNull;

/**
 * This does not represent data actually returned by CouchDB, it is just here to help group stuff together
 */
public class DocumentData {
	private final String revision;
	private final String json;

	public DocumentData(String revision, String json) {
		requireNonNull(this.revision = revision);
		requireNonNull(this.json = json);
	}

	public String getRevision() {
		return revision;
	}

	public String getJson() {
		return json;
	}
}
