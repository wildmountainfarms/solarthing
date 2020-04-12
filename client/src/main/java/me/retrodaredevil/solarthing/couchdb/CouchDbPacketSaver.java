package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.DocumentWrapper;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbAccessException;
import org.ektorp.UpdateConflictException;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.impl.StdObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CouchDbPacketSaver implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketSaver.class);
//	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final Map<String, DocumentWrapper> idMap = new HashMap<>(); // TODO we could probably figure out a way to clear old values
	private final CouchDbConnector client;


	public CouchDbPacketSaver(CouchProperties properties, String databaseName){
		final HttpClient httpClient = EktorpUtil.createHttpClient(properties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		client = new StdCouchDbConnector(databaseName, instance, new StdObjectMapperFactory(){
			@Override
			protected void applyDefaultConfiguration(ObjectMapper om) {
//				super.applyDefaultConfiguration(om);
				JacksonUtil.defaultMapper(om);
			}
		});
	}

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		try {
			client.createDatabaseIfNotExists();
		} catch(DbAccessException ex){
			throw new PacketHandleException("Could not establish connection", ex);
		}

		final String id = packetCollection.getDbId();
		final DocumentWrapper object;
		{
			final DocumentWrapper currentValue = idMap.get(id);
			if(currentValue == null){
				object = new DocumentWrapper(id);
				idMap.put(id, object);
			} else {
				object = currentValue;
			}
		}
		object.setObject(packetCollection);
		try {
			if (object.getRevision() == null) {
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

}
