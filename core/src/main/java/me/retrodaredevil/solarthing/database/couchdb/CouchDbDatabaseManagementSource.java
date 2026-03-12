package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.solarthing.database.DatabaseManagementSource;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class CouchDbDatabaseManagementSource implements DatabaseManagementSource {
	private final CouchDbInstance instance;

	public CouchDbDatabaseManagementSource(CouchDbInstance instance) {
		this.instance = requireNonNull(instance);
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
