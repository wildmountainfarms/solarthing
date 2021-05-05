package me.retrodaredevil.couchdbjava.response;

import me.retrodaredevil.couchdbjava.json.JsonData;

import static java.util.Objects.requireNonNull;

/**
 * This does not represent data actually returned by CouchDB, it is just here to help group stuff together
 */
public class DocumentData {
	private final String revision;
	private final JsonData json;

	public DocumentData(String revision, JsonData json) {
		requireNonNull(this.revision = revision);
		requireNonNull(this.json = json);
	}

	public String getRevision() {
		return revision;
	}

	public JsonData getJsonData() {
		return json;
	}
}
