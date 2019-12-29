package me.retrodaredevil.solarthing.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.bson.Document;

public class MongoPacketSaver implements PacketHandler {
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final MongoClient client;
	private final String databaseName;
	private final String collectionName;
	
	public MongoPacketSaver(MongoClient client, String databaseName, String collectionName) {
		this.client = client;
		this.databaseName = databaseName;
		this.collectionName = collectionName;
	}
	
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(collectionName);
		final String json;
		try {
			json = MAPPER.writeValueAsString(packetCollection);
		} catch (JsonProcessingException e) {
			throw new PacketHandleException("Could not write json!", e);
		}
		collection.insertOne(Document.parse(json));
		try {
			ClientSession session = client.startSession();
			session.commitTransaction();
		} catch(Exception e){
			throw new PacketHandleException(e);
		}
	}
}
