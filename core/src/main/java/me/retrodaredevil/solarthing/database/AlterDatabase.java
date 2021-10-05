package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.annotations.WorkInProgress;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;

@WorkInProgress
public interface AlterDatabase {

	void delete(String documentId, UpdateToken updateToken) throws SolarThingDatabaseException;
}
