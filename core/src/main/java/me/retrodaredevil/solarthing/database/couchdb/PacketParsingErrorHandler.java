package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;

public interface PacketParsingErrorHandler {
	void handleError(Exception exception) throws SolarThingDatabaseException;

	PacketParsingErrorHandler DO_NOTHING = (ex) -> {};
}
