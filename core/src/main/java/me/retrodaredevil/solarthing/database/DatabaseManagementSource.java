package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;

public interface DatabaseManagementSource {
	void checkConnection() throws SolarThingDatabaseException;
}
