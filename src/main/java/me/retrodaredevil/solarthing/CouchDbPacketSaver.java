package me.retrodaredevil.solarthing;

import com.google.gson.JsonObject;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.util.json.JsonFile;
import org.lightcouch.*;

import java.util.HashMap;
import java.util.Map;

public class CouchDbPacketSaver implements PacketHandler {
	private final Map<String, String> idRevMap = new HashMap<>();
	private final CouchProperties properties;
	private CouchDbClient client = null;

	public CouchDbPacketSaver(CouchProperties properties, String databaseName){
		this.properties = new CouchPropertiesBuilder(properties).setDatabase(databaseName).build();
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
		final String id = packetCollection.getDbId();
		final String rev = idRevMap.get(id);
		final Response response;
		final JsonObject packet = JsonFile.gson.toJsonTree(packetCollection).getAsJsonObject();
		try {
			if (rev == null) {
				response = client.save(packet);
			} else {
				packet.addProperty("_rev", rev);
				response = client.update(packet);
			}
		} catch(DocumentConflictException ex){
			throw new PacketHandleException("Error while saving something to couchdb. Your throttle factor (--tf) may be too low or you recently restarted the program.", ex);
		} catch(CouchDbException ex){
			throw new PacketHandleException("We got a CouchDbException probably meaning we couldn't reach the database.", ex);
		}
		idRevMap.put(id, response.getRev());
	}

}
