package me.retrodaredevil.solarthing.program;

import com.google.gson.JsonObject;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.solarthing.JsonPacketReceiver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import org.lightcouch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CouchDbPacketRetriever implements PacketHandler {
	private final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketRetriever.class);
	
	private final CouchProperties properties;
	private final JsonPacketReceiver jsonPacketReceiver;
	private final boolean removeQueriedPackets;
	private CouchDbClient client = null;
	
	public CouchDbPacketRetriever(CouchProperties properties, String databaseName, JsonPacketReceiver jsonPacketReceiver, boolean removeQueriedPackets){
		this.jsonPacketReceiver = jsonPacketReceiver;
		this.properties = new CouchPropertiesBuilder(properties).setDatabase(databaseName).setCreateIfNotExist(false).build();
		this.removeQueriedPackets = removeQueriedPackets;
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
			JsonObject baseObject = packets.get(i);
			JsonObject jsonObject = baseObject.getAsJsonObject("value");
			packets.set(i, jsonObject);
			if(removeQueriedPackets) {
				String id = jsonObject.get("_id").getAsString();
				String rev = jsonObject.get("_rev").getAsString();
				try {
//					client.remove(jsonObject);
					// TODO figure out a way to do a bulk remove
					Response response = client.remove(id, rev);
					String error = response.getError();
					if(response.getError() != null){
						LOGGER.warn("Got error while removing! error='" + error + "'. Reason='" + response.getReason() + "' id='" + response.getId() + "' rev='" + response.getRev() + "'");
					}
				} catch(CouchDbException ex){
					LOGGER.warn("Unable to remove id='" + id + "' with rev='" + rev + "'", ex);
				}
			}
		}
		jsonPacketReceiver.receivePackets(packets);
	}
	
}
