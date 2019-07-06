package me.retrodaredevil.solarthing.couchdb;

import com.google.gson.JsonObject;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.View;

import java.util.List;

public abstract class CouchDbPacketRetriever implements PacketHandler {
	private final CouchProperties properties;
	private CouchDbClient client = null;
	
	public CouchDbPacketRetriever(CouchProperties properties, String databaseName){
		this.properties = new CouchPropertiesBuilder(properties).setDatabase(databaseName).setCreateIfNotExist(false).build();
	}
	
	protected abstract void receivePackets(List<JsonObject> packets);
	
	protected View alterView(View view){
		return view;
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		final CouchDbClient currentClient = this.client;
		final CouchDbClient client;
		if(currentClient == null){
			try {
				client = new CouchDbClient(properties.createProperties());
			} catch(CouchDbException ex) {
				throw new PacketHandleException("We couldn't connect to the database for the first time!", ex);
			}
			this.client = client;
		} else {
			client = currentClient;
		}
		List<JsonObject> packets = alterView(client.view("packets/millis")).query(JsonObject.class); // all packets
		for(JsonObject packet : packets){
			client.remove(packet); // TODO figure out a way to do a bulk remove
		}
		receivePackets(packets);
	}
	
}
