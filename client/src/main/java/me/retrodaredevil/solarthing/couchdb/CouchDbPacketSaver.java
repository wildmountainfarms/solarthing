package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CouchDbPacketSaver implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketSaver.class);
	private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.defaultMapper();

	private final Map<String, String> idRevMap = new HashMap<>(); // TODO we could probably figure out a way to clear old values
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
		final String jsonPacket;
		try {
			jsonPacket = OBJECT_MAPPER.writeValueAsString(packetCollection); // use jackson
		} catch (JsonProcessingException e) {
			throw new RuntimeException("This should not happen!", e);
		}
		final JsonObject packet = JsonParser.parseString(jsonPacket).getAsJsonObject(); // convert to GSON
		packet.addProperty("_id", id);
		try {
			if (rev == null) {
				response = client.save(packet);
			} else {
				packet.addProperty("_rev", rev);
				response = client.update(packet);
			}
		} catch(DocumentConflictException ex){
			try {
				InputStream documentStream = client.find(id);
				final ObjectNode document = OBJECT_MAPPER.readValue(documentStream, ObjectNode.class);
				String actualRev = document.get("_rev").asText();
				idRevMap.put(id, actualRev);
				LOGGER.info("We were able to get the actual Revision ID for id=" + id + " actual rev=" + actualRev);
			} catch(CouchDbException | IOException revEx){ // We have to catch these json related exceptions because of a bug in CouchDB
				LOGGER.warn("Unable to get the actual Revision ID for id=" + id, revEx);
			}
			throw new PacketHandleException("Conflict while saving something to couchdb. id=" + id + " rev=" + rev + ". This usually means we put a packet in the database, but we weren't able to cache its rev id.", ex);
		} catch(CouchDbException ex){
			throw new PacketHandleException("We got a CouchDbException probably meaning we couldn't reach the database.", ex);
		}
		idRevMap.put(id, response.getRev());
	}

}
