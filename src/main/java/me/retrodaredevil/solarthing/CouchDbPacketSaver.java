package me.retrodaredevil.solarthing;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.PacketSaver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.util.json.JsonFile;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.HashMap;
import java.util.Map;

public class CouchDbPacketSaver implements PacketSaver {
	private final Map<String, String> idRevMap = new HashMap<>();
	private final CouchDbClient client;

	public CouchDbPacketSaver(CouchDbProperties properties, String databaseName){
		properties.setDbName(databaseName);
		client = new CouchDbClient(properties);
	}

	@Override
	public void savePacketCollection(PacketCollection packetCollection) {
		final String id = packetCollection.getDbId();
		final String rev = idRevMap.get(id);
		final String newRev;
		final JsonObject packet = JsonFile.gson.toJsonTree(packetCollection).getAsJsonObject();
		if(rev == null){
			newRev = client.save(packet).getRev();
		} else {
			packet.addProperty("_rev", rev);
			newRev = client.update(packet).getRev();
		}
		idRevMap.put(id, newRev);
	}

}
