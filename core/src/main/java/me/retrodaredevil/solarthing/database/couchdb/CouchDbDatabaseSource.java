package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.solarthing.database.DatabaseSource;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class CouchDbDatabaseSource implements DatabaseSource {
	private final CouchDbDatabase database;

	public CouchDbDatabaseSource(CouchDbDatabase database) {
		this.database = requireNonNull(database);
	}

	@Override
	public boolean exists() throws SolarThingDatabaseException {
		try {
			return database.exists();
		} catch (CouchDbException e) {
			throw ExceptionUtil.createFromCouchDbException(e);
		}
	}
}
