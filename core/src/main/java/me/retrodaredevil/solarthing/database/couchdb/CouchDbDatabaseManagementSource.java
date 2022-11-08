package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.solarthing.database.DatabaseManagementSource;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;

import static java.util.Objects.requireNonNull;

public class CouchDbDatabaseManagementSource implements DatabaseManagementSource {
	private final CouchDbInstance instance;

	public CouchDbDatabaseManagementSource(CouchDbInstance instance) {
		requireNonNull(this.instance = instance);
	}

	@Override
	public void checkConnection() throws SolarThingDatabaseException {
		try {
			instance.getInfo();
		} catch (CouchDbException e) {
			throw ExceptionUtil.createFromCouchDbException(e);
		}
	}
}
