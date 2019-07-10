package me.retrodaredevil.solarthing.couchdb;

import com.google.gson.JsonObject;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.solarthing.JsonPacketReceiver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.util.json.JsonFile;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.NoDocumentException;
import org.lightcouch.View;

import java.util.List;

public class CouchDbPacketRetriever implements PacketHandler {
	private final CouchProperties properties;
	private final JsonPacketReceiver jsonPacketReceiver;
	private CouchDbClient client = null;
	
	public CouchDbPacketRetriever(CouchProperties properties, String databaseName, JsonPacketReceiver jsonPacketReceiver){
		this.jsonPacketReceiver = jsonPacketReceiver;
		this.properties = new CouchPropertiesBuilder(properties).setDatabase(databaseName).setCreateIfNotExist(false).build();
	}
	
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
		final List<JsonObject> packets;
		View view = alterView(client.view("packets/millis"));
		try {
			// This query returns a new ArrayList that we can mutate without side effects
			packets = view.query(JsonObject.class); // all packets
		} catch(NoDocumentException e){
			throw new PacketHandleException("No document exception... Maybe the 'millis' view hasn't been created in design 'packets'?", e);
		} catch(CouchDbException e){
			throw new PacketHandleException("This probably means we couldn't reach the database", e);
		}
		for(int i = 0; i < packets.size(); i++){
			JsonObject jsonObject = packets.get(i).getAsJsonObject("value");
			packets.set(i, jsonObject);
			System.out.println("removing: " + JsonFile.gson.toJson(jsonObject));
			client.remove(jsonObject); // TODO figure out a way to do a bulk remove
		}
		jsonPacketReceiver.receivePackets(packets);
	}
	
}
