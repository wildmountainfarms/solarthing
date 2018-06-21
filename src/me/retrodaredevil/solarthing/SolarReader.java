package me.retrodaredevil.solarthing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packet.PacketCollection;
import me.retrodaredevil.solarthing.packet.SolarPacket;
import me.retrodaredevil.solarthing.util.json.JsonFile;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.DocumentConflictException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SolarReader extends Thread{
	private static final long SAME_PACKET_COLLECTION_TIME = 250;

	private final int throttleFactor;
	private final InputStream in;
	//private ProgramArgs args;

	private final CouchDbClient client;
	private final JsonFile jsonFile;


	public SolarReader(InputStream in, ProgramArgs args) throws IOException {
		this.in = in;
		this.throttleFactor = args.getThrottleFactor();
		//this.args = args;
		makeCouchQuiet();
		client = createClient(args);
		if(client == null){
			args.printInJson();
			File file = new File("data.json");
			if(!file.exists()){
				file.createNewFile();
			}
			jsonFile = new JsonFile(file);
			if(!jsonFile.isJson()){
				jsonFile.load(true);
			}
		} else {
			jsonFile = null;
		}
	}
	/** @return The CouchDbClient using the given ProgramArgs or null if a connection could not be established */
	private static CouchDbClient createClient(ProgramArgs args){
		try{
			CouchDbClient client = new CouchDbClient(args.getProperties());
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

	@Override
	public void run() {
		final PacketCreator creator = new PacketCreator49();
		final List<SolarPacket> packetList = new ArrayList<>(); // a list that piles up SolarPackets and saves when needed
		long lastFirstReceivedData = Long.MIN_VALUE; // the last time a packet was added to packetList

		int packetCollectionCounter = -1;

		final byte[] buffer = new byte[1024];
		int len;

		while(true) {
			try {
				// ======= read bytes, append to packetList =======
				while (this.in.available() > 0 && (len = this.in.read(buffer)) > -1) {
					String s = new String(buffer, 0, len);
//					System.out.println("got characters: '" + s +"'");
					Collection<SolarPacket> newPackets = creator.add(s.toCharArray()); // usually null or size of 1 possibly size > 1

					long now = System.currentTimeMillis();
					if(lastFirstReceivedData + SAME_PACKET_COLLECTION_TIME < now) {
						lastFirstReceivedData = now; // set this to the first time we get bytes
					}

					if (newPackets != null) { // packets.length should never be 0 if it's not null
						packetList.addAll(newPackets);
					}
				}

				// ======= Save data if needed =======
				long now = System.currentTimeMillis();
				if (lastFirstReceivedData + SAME_PACKET_COLLECTION_TIME < now) { // if there's no packets coming any time soon
					if(!packetList.isEmpty()) {
						packetCollectionCounter++;
						// because packetCollectionCounter starts at -1, after above if statement, it will be >= 0
						if(packetCollectionCounter % throttleFactor == 0) {
							System.out.println("saving above packet(s). packetList.size(): " + packetList.size());
							PacketCollection packetCollection = new PacketCollection(new ArrayList<>(packetList));
							packetList.clear();
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
						} else {
							System.out.println("Not saving above packet(s) because " +
									"throttleFactor: " + throttleFactor +
									" packetCollectionCounter: " + packetCollectionCounter);
							packetList.clear(); // don't save these packets - ignore them
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch(DocumentConflictException ex){
				ex.printStackTrace();
				System.err.println("Error while saving something to couchdb. Continuing like nothing happened now. " +
						"Your throttle factor (--tf) may be too low.");
			}
		}
	}
}
