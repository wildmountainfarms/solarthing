package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.couchdb.CouchProperties;

public class CouchDbEnvironment {
	private final CouchProperties couchProperties;

	public CouchDbEnvironment(CouchProperties couchProperties) {
		this.couchProperties = couchProperties;
	}

	public CouchProperties getCouchProperties() {
		return couchProperties;
	}
}
