package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbAccessException;
import org.ektorp.UpdateConflictException;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CouchDbPacketSaver implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketSaver.class);
//	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final Map<String, ObjectWrapper> idMap = new HashMap<>(); // TODO we could probably figure out a way to clear old values
	private final CouchDbConnector client;


	public CouchDbPacketSaver(CouchProperties properties, String databaseName){
		final HttpClient httpClient = EktorpUtil.createHttpClient(properties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		client = new StdCouchDbConnector(databaseName, instance);
	}

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		try {
			client.createDatabaseIfNotExists();
		} catch(DbAccessException ex){
			throw new PacketHandleException("Could not establish connection", ex);
		}

		final String id = packetCollection.getDbId();
		final ObjectWrapper object;
		{
			final ObjectWrapper currentValue = idMap.get(id);
			if(currentValue == null){
				object = new ObjectWrapper(id);
				idMap.put(id, object);
			} else {
				object = currentValue;
			}
		}
		object.object = packetCollection;
		try {
			if (object.rev == null) {
				client.create(object);
			} else {
				client.update(object);
			}
		} catch(UpdateConflictException ex){
			try {
				String actualRev = client.getCurrentRevision(id);
				object.setRevision(actualRev);
				LOGGER.info("We were able to get the actual Revision ID for id=" + id + " actual rev=" + actualRev);
			} catch(DbAccessException revEx){
				LOGGER.warn("Unable to get the actual Revision ID for id=" + id, revEx);
			}
			throw new PacketHandleException("Conflict while saving something to couchdb. id=" + id + " rev=" + object.getRevision() + ". This usually means we put a packet in the database, but we weren't able to cache its rev id.", ex);
		} catch(DbAccessException ex){
			throw new PacketHandleException("We got a DbAccessException probably meaning we couldn't reach the database.", ex);
		}
	}

	@JsonExplicit
	private static class ObjectWrapper {
		private final String id;
		private String rev = null;

		@JsonUnwrapped
		private PacketCollection object = null;

		private ObjectWrapper(String id) {
			this.id = id;
		}

		@JsonGetter("_id")
		public String getId(){
			return id;
		}

		@JsonGetter("_rev")
		public String getRevision(){
			return rev;
		}
		@JsonSetter("_rev")
		public void setRevision(String rev){
			this.rev = rev;
		}
	}
}
