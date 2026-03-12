package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface AlterDatabase {
	UpdateToken upload(StoredAlterPacket storedAlterPacket) throws SolarThingDatabaseException;
	// TODO sourceId is not used in implementations
	List<VersionedPacket<StoredAlterPacket>> queryAll(String sourceId) throws SolarThingDatabaseException;
	void delete(String documentId, UpdateToken updateToken) throws SolarThingDatabaseException;

	default void delete(VersionedPacket<? extends StoredAlterPacket> versionedPacket) throws SolarThingDatabaseException {
		delete(versionedPacket.getPacket().getDbId(), versionedPacket.getUpdateToken());
	}

	DatabaseSource getDatabaseSource();
}
