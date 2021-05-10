package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;

import java.util.List;

public interface MillisDatabase {
	List<PacketGroup> query(MillisQuery query) throws SolarThingDatabaseException;

	UpdateToken uploadPacketCollection(PacketCollection packetCollection, UpdateToken updateToken) throws SolarThingDatabaseException;
	UpdateToken getCurrentUpdateToken(String documentId) throws SolarThingDatabaseException;
}
