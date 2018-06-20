package me.retrodaredevil.solarthing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.retrodaredevil.solarthing.packet.SolarPacket;
import me.retrodaredevil.solarthing.util.json.JsonFile;

public class SolarReader implements Runnable{
	private static final long SAME_PACKET_COLLECTION_TIME = 250;

	private final int throttleFactor;
	private InputStream in;
	//private ProgramArgs args;
	private PacketCreator creator = null;
	private int packetCollectionCounter = -1;
	private long lastFirstSavePacket = Long.MIN_VALUE;
	
	private CouchDbClient client;
	private JsonFile jsonFile;
	

	public SolarReader(InputStream in, ProgramArgs args) throws IOException {
		this.in = in;
		this.throttleFactor = args.getThrottleFactor();
		//this.args = args;
		try{
			client = new CouchDbClient(args.getProperties());
			Logger logger = Logger.getLogger("com.couchbase.client");
			Level level = Level.INFO;
			logger.setLevel(level);
			for(Handler h : logger.getParent().getHandlers()) {
				if(h instanceof ConsoleHandler){
					h.setLevel(level);
				}
			}
			System.out.println("Connecting to database worked!");
		} catch (CouchDbException ex){
			ex.printStackTrace();
			System.err.println("Couldn't connect to data base. data going into ./data.json");
			args.printInJson();
			File file = new File("data.json");
			if(!file.exists()){
				file.createNewFile();
			}
			jsonFile = new JsonFile(file);
			if(!jsonFile.isJson()){
				jsonFile.load(true);
			}
		}
		
	}
	private void initPacketCreator(){
		creator = new PacketCreator49();
	}

	public void run() {
		while(true){
			byte[] buffer = new byte[1024];
			int len = -1;
			
			if(creator == null){
				initPacketCreator();
			}
			try {
				while ((len = this.in.read(buffer)) > -1) {
					String s = new String(buffer, 0, len);
//					System.out.println("got characters: '" + s +"'");
					Collection<SolarPacket> packets = creator.add(s.toCharArray());
					
					if(packets != null){ // packets.length should never be 0 if it's not null
						long now = System.currentTimeMillis();
						if(lastFirstSavePacket + SAME_PACKET_COLLECTION_TIME < now){
							lastFirstSavePacket = now;
							packetCollectionCounter++; // starts at -1
						}
						if(packetCollectionCounter % throttleFactor == 0){
							System.out.println("saving above packet(s).");
							for(SolarPacket p : packets){
								if(client != null){
									client.save(p);
								} else {

									JsonElement el = jsonFile.getObject().get("packets"); // can be null
									if(el == null){
										el = new JsonArray();
										jsonFile.getObject().add("packets", el);
									}
									if(el instanceof JsonArray){
										JsonArray ar = (JsonArray) el;
										JsonObject add = (JsonObject) JsonFile.pa.parse(JsonFile.gson.toJson(p));
										ar.add(add);
									} else {
										throw new IllegalStateException("The JsonElement retreived from packets, must be a JsonArray");
									}
									jsonFile.save();
								}
							}
						} else {
							System.out.println("Not saving above packet(s) because " +
									"throttleFactor: " + throttleFactor +
									" packetCollectionCounter: " + packetCollectionCounter);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
	}
}
