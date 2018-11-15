package me.retrodaredevil.solarthing;

import org.lightcouch.CouchDbClient;

import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.retrodaredevil.ProgramArgs;
import me.retrodaredevil.solarthing.packet.PacketCollection;
import me.retrodaredevil.solarthing.packet.Packet;
import me.retrodaredevil.solarthing.packet.PacketCollections;

public class CouchDbPacketSaver implements PacketSaver {
	private final CouchDbClient client;

	public CouchDbPacketSaver(ProgramArgs args){
		client = createClient(args);
	}

	/** @return The CouchDbClient using the given ProgramArgs or null if a connection could not be established */
	private static CouchDbClient createClient(ProgramArgs args){
		return new CouchDbClient(args.getProperties());
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
