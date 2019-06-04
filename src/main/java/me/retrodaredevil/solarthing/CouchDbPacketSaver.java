package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.PacketSaver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class CouchDbPacketSaver implements PacketSaver {
	private final CouchDbClient client;

	public CouchDbPacketSaver(ProgramArgs args, String databaseName){
		client = createClient(args, databaseName);
	}

	/** @return The CouchDbClient using the given ProgramArgs or null if a connection could not be established */
	private static CouchDbClient createClient(ProgramArgs args, String databaseName){
		CouchDbProperties properties = args.getProperties();
		properties.setDbName(databaseName);
		return new CouchDbClient(properties);
	}
	@Override
	public void savePacketCollection(PacketCollection packetCollection) {
		client.save(packetCollection);
	}

}
