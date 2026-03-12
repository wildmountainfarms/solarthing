package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DatabaseManagementSource {
	void checkConnection() throws SolarThingDatabaseException;
}
