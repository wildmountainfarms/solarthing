package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;

import java.util.List;

public interface AlterDatabase {
	@NotNull UpdateToken upload(StoredAlterPacket storedAlterPacket) throws SolarThingDatabaseException;
	@NotNull List<VersionedPacket<StoredAlterPacket>> queryAll(String sourceId) throws SolarThingDatabaseException;
	void delete(String documentId, UpdateToken updateToken) throws SolarThingDatabaseException;

	default void delete(VersionedPacket<? extends StoredAlterPacket> versionedPacket) throws SolarThingDatabaseException {
		delete(versionedPacket.getPacket().getDbId(), versionedPacket.getUpdateToken());
	}
}
