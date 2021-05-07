package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.design.DefaultPacketsDesign;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUpdateConflictException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
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
	private final Map<String, String> idMap = new HashMap<>(); // TODO we could probably figure out a way to clear old values
	private final CouchDbDatabase database;
	private final boolean addDefaultDesign;

	private boolean defaultDesignAdded = false;

	public CouchDbPacketSaver(CouchDbDatabase database, boolean addDefaultDesign){
		this.database = database;
		this.addDefaultDesign = addDefaultDesign;
	}
	public CouchDbPacketSaver(CouchDbDatabase database){
		this(database, true);
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
		try {
			database.createIfNotExists();
		} catch(CouchDbException ex){
			throw new PacketHandleException("Could not establish connection", ex);
		}
		if (addDefaultDesign && !defaultDesignAdded) {
			final String json;
			try {
				json = MAPPER.writeValueAsString(new DefaultPacketsDesign());
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Should be able to serialize this design to json!", e);
			}
			try {
				database.putDocument("_design/packets", new StringJsonData(json));
				defaultDesignAdded = true;
				LOGGER.info("Created default design document on database=" + database.getName());
			} catch(CouchDbUpdateConflictException ex) {
				defaultDesignAdded = true;
				LOGGER.debug("Already had a design document for packets on database=" + database.getName());
			} catch(CouchDbException ex) {
				LOGGER.debug("Couldn't create default design", ex);
			}
		}

		String id = packetCollection.getDbId();
		String revision = idMap.get(id);
		final JsonData jsonData;
		try {
			jsonData = new StringJsonData(MAPPER.writeValueAsString(packetCollection));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Cannot serialize packet collection! This is bad!", e);
		}
		try {
			if (revision == null) {
				database.putDocument(id, jsonData);
			} else {
				database.updateDocument(id, revision, jsonData);
			}
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
