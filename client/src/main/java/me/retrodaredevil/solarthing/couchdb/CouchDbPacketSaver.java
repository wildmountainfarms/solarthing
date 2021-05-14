package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.design.DefaultPacketsDesign;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotFoundException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUpdateConflictException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CouchDbPacketSaver implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketSaver.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	/** A map of document IDs to the current revision of that document */
	private final Map<String, String> idMap = new HashMap<>();
	private final CouchDbDatabase database;

	public CouchDbPacketSaver(CouchDbDatabase database){
		this.database = database;
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
		// Normally we would try and create the database, but that doesn't work with non-admin cookie authenticated users

		String id = packetCollection.getDbId();
		String revision = idMap.get(id);
		final JsonData jsonData;
		try {
			jsonData = new StringJsonData(MAPPER.writeValueAsString(packetCollection));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Cannot serialize packet collection! This is bad!", e);
		}
		try {
			final DocumentResponse response;
			if (revision == null) {
				response = database.putDocument(id, jsonData);
			} else {
				response = database.updateDocument(id, revision, jsonData);
			}
			LOGGER.debug("Now revision is: " + response.getRev() + ". It was: " + revision);
			idMap.clear(); // Currently, if we have a new document ID, we never, ever, need to worry about using an older document ID, so we can clear the map to avoid keeping unnecessary memory
			idMap.put(id, response.getRev());
		} catch (CouchDbNotFoundException ex) {
			throw new PacketHandleException("Got 'not found'. Does the database exist? Make sure to run the couchdb-setup!", ex);
		} catch(CouchDbUpdateConflictException ex){
			try {
				String actualRev = database.getCurrentRevision(id);
				idMap.put(id, actualRev);
				LOGGER.info("We were able to get the actual Revision ID for id=" + id + " actual rev=" + actualRev);
			} catch(CouchDbException revEx){
				LOGGER.warn("Unable to get the actual Revision ID for id=" + id, revEx);
			}
			throw new PacketHandleException("Conflict while saving something to couchdb. id=" + id + " rev=" + revision + ". This usually means we put a packet in the database, but we weren't able to cache its rev id.", ex);
		} catch(CouchDbException ex){
			if (ex.getCause() instanceof IOException) {
				throw new PacketHandleException("We got a DbAccessException probably meaning we couldn't reach the database.", ex);
			} else {
				throw new PacketHandleException("Got a DbAccessException without IOException as a cause. Something must be wrong.", ex);
			}
		}
	}

}
