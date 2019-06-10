package me.retrodaredevil.solarthing;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.PacketSaveException;
import me.retrodaredevil.solarthing.packets.PacketSaver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.util.json.JsonFile;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.DocumentConflictException;

import java.util.HashMap;
import java.util.Map;

public class CouchDbPacketSaver implements PacketSaver {
	private final Map<String, String> idRevMap = new HashMap<>();
	private final CouchDbProperties properties;
	private CouchDbClient client = null;

	public CouchDbPacketSaver(CouchDbProperties properties, String databaseName){
		properties.setDbName(databaseName);
		this.properties = properties;
	}

	@Override
	public void savePacketCollection(PacketCollection packetCollection) throws PacketSaveException {
		final CouchDbClient currentClient = this.client;
		final CouchDbClient client;
		if(currentClient == null){
			try {
				client = new CouchDbClient(properties);
			} catch(CouchDbException ex) {
				throw new PacketSaveException("We couldn't connect to the database for the first time!", ex);
			}
			this.client = client;
		} else {
			client = currentClient;
		}
		final String id = packetCollection.getDbId();
		final String rev = idRevMap.get(id);
		final String newRev;
		final JsonObject packet = JsonFile.gson.toJsonTree(packetCollection).getAsJsonObject();
		try {
			if (rev == null) {
				newRev = client.save(packet).getRev();
			} else {
				packet.addProperty("_rev", rev);
				newRev = client.update(packet).getRev();
			}
		} catch(DocumentConflictException ex){
			throw new PacketSaveException("Error while saving something to couchdb. Your throttle factor (--tf) may be too low or you recently restarted the program.", ex);
		} catch(CouchDbException ex){
			throw new PacketSaveException("We got a CouchDbException probably meaning we couldn't reach the database.", ex);
		}
		idRevMap.put(id, newRev);
	}

}
