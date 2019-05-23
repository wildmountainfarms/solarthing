package me.retrodaredevil.iot;

import me.retrodaredevil.iot.packets.Packet;
import me.retrodaredevil.iot.packets.PacketCollection;
import me.retrodaredevil.iot.packets.PacketCollections;
import me.retrodaredevil.iot.packets.PacketSaver;
import org.lightcouch.CouchDbClient;

import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.retrodaredevil.ProgramArgs;
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
	/** Assuming the log4j library is running, this stops CouchDb from spitting out logs every time we save*/
	private static void makeCouchQuiet(){
		Logger logger = Logger.getLogger("com.couchbase.client");
		Level level = Level.INFO;
		logger.setLevel(level);
		for(Handler h : logger.getParent().getHandlers()) {
			if(h instanceof ConsoleHandler){
				h.setLevel(level);
			}
		}
	}
	@Override
	public void savePacketCollection(PacketCollection packetCollection) {
		client.save(packetCollection);
	}

	@Override
	public void savePackets(Collection<Packet> packets) {
		savePacketCollection(PacketCollections.createFromPackets(packets));
	}
}
