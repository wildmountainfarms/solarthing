package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;

import java.util.List;

public interface MillisDatabase {
	List<StoredPacketGroup> query(MillisQuery query) throws SolarThingDatabaseException;

	UpdateToken uploadPacketCollection(PacketCollection packetCollection, UpdateToken updateToken) throws SolarThingDatabaseException;
	VersionedPacket<StoredPacketGroup> getPacketCollection(String documentId) throws SolarThingDatabaseException;

	/**
	 * @deprecated  This method was implemented a while back for completeness's sake. Know that internally this may not work with PouchDB
	 */
	@Deprecated
	UpdateToken getCurrentUpdateToken(String documentId) throws SolarThingDatabaseException;

	DatabaseSource getDatabaseSource();
}
