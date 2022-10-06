package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;

/**
 * Represents an individual database of a {@link DatabaseManagementSource}
 */
public interface DatabaseSource {
	/**
	 * Note: It is possible for the database to exist and an exception to be thrown. Commonly, {@link me.retrodaredevil.solarthing.database.exception.UnauthorizedSolarThingDatabaseException} may be thrown
	 * upon the request of information about a database that has strict permissions on it
	 *
	 * @return true if this database exists
	 * @throws SolarThingDatabaseException For connection exceptions, auth exceptions, and other exceptions
	 */
	boolean exists() throws SolarThingDatabaseException;
}
