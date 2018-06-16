package me.retrodaredevil.solarthing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.retrodaredevil.solarthing.packet.SolarPacket;
import me.retrodaredevil.solarthing.util.json.JsonFile;

public class SolarReader implements Runnable{
	
	//private static final CouchDbProperties prop = new CouchDbProperties("solarthing", true, "http", "127.0.0.1", 5984, "admin", "relax");
	
	private InputStream in;
	//private ProgramArgs args;
	private PacketCreator creator = null;
	
	private CouchDbClient client;
	
	private JsonFile jsonFile;
	

	public SolarReader(InputStream in, ProgramArgs args) throws IOException {
		this.in = in;
		//this.args = args;
		try{
			client = new CouchDbClient(args.getProperties());
			System.out.println("Hey! It worked!");
		} catch (CouchDbException ex){
			ex.printStackTrace();
			System.err.println("Couldn't connect to data base.");
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
					
					
					if(packets != null){
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
							}
						}
					}
	
					if(jsonFile != null){
						jsonFile.save();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
	}
}
