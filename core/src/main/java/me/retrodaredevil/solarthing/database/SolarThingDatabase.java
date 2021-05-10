package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.meta.RootMetaPacket;

public interface SolarThingDatabase {
	MillisDatabase getStatusDatabase();
	MillisDatabase getEventDatabase();
	MillisDatabase getOpenDatabase();

	VersionedPacket<RootMetaPacket> queryMetadata(UpdateToken updateToken) throws SolarThingDatabaseException;
	VersionedPacket<AuthorizationPacket> queryAuthorized(UpdateToken updateToken) throws SolarThingDatabaseException;
}
