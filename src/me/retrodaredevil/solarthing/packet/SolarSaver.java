package me.retrodaredevil.solarthing.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.ProgramArgs;
import me.retrodaredevil.solarthing.util.json.JsonFile;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.CouchDbProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @deprecated class doesn't work, code is here just to keep code that may be used in the future if we are forced to
 */
@Deprecated
public class SolarSaver extends Thread { // never again
	private static final long SAME_PACKET_COLLECTION_TIME = 250;

	private final int throttleFactor;

	private final CouchDbProperties databaseProperties;
//	private final CouchDbClient client;
//	private final JsonFile jsonFile;

	private final Queue<SolarPacket> packetQueue = new ConcurrentLinkedQueue<>(); // ArrayBlockingQueue or ConcurrentLinkedQueue
	private volatile long lastCompletePacket = Long.MIN_VALUE;

	public SolarSaver(ProgramArgs args) throws IOException {
		this.throttleFactor = args.getThrottleFactor();
		this.databaseProperties = args.getProperties();
		makeCouchQuiet();
	}

	/** @return The CouchDbClient using the given ProgramArgs or null if a connection could not be established */
	private static CouchDbClient createClient(CouchDbProperties properties){
		try{
			CouchDbClient client = new CouchDbClient(properties);
			System.out.println("Connecting to database worked!");
			return client;
		} catch(CouchDbException ex){
			ex.printStackTrace();
			System.err.println("Couldn't connect to data base.");
			return null;
		}
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

	public void addPackets(Collection<SolarPacket> packets){ // accessed from other threads
		this.lastCompletePacket = System.currentTimeMillis();
		this.packetQueue.addAll(packets);
		this.lastCompletePacket = System.currentTimeMillis();
	}

	private void savePackets(CouchDbClient client, JsonFile jsonFile, PacketCollection packetCollection) throws IOException { // called from this thread
		if (client != null) {
			client.save(packetCollection);
		} else {

			JsonElement el = jsonFile.getObject().get("packets"); // can be null
			if (el == null) {
				el = new JsonArray();
				jsonFile.getObject().add("packets", el);
			}
			if (el instanceof JsonArray) {
				JsonArray ar = (JsonArray) el;
				JsonObject add = (JsonObject) JsonFile.pa.parse(JsonFile.gson.toJson(packetCollection));
				ar.add(add);
			} else {
				throw new IllegalStateException("The JsonElement retrieved from packet must be a JsonArray");
			}
			jsonFile.save();
		}
	}


	@Override
	public void run() {

		final CouchDbClient client = createClient(databaseProperties);
		JsonFile jsonFile = null;
		if(client == null){
			File file = new File("data.json");
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				jsonFile = new JsonFile(file);
				if (!jsonFile.isJson()) {
					jsonFile.load(true);
				}
			} catch(FileNotFoundException ex){
				throw new RuntimeException("This should never happen! We checked to make sure the file existed!");
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		int packetCollectionCounter = -1;
//		long lastPacketCollectionSave = Long.MIN_VALUE; // the last time we saved a packetList as a PacketCollection

		while(true){
			if(lastCompletePacket + SAME_PACKET_COLLECTION_TIME < System.currentTimeMillis() && !packetQueue.isEmpty()){
				packetCollectionCounter++;
				if(packetCollectionCounter % throttleFactor == 0) {
					try {
						int size = packetQueue.size();
						SolarPacket[] packetArray = new SolarPacket[size];
						for(int i = 0; i < size; i++){
							SolarPacket packet = packetQueue.poll();
							System.out.println(JsonFile.gson.toJson(packet));
							packetArray[i] = packet;
						}
						PacketCollection packetCollection = null;//= new PacketCollection(packetArray);
						System.out.println("starting save of above packet(s). size: " + size);
						savePackets(client, jsonFile, packetCollection);
						System.out.println("Successfully saved above packet(s).");
					} catch (IOException ex) {
						ex.printStackTrace();
						System.out.println("Couldn't save above packet(s).");
					}
				} else {
					System.out.println("Not saving above packet(s) because " +
							"throttleFactor: " + throttleFactor +
							" packetCollectionCounter: " + packetCollectionCounter);
				}
			}
		}
	}
}
