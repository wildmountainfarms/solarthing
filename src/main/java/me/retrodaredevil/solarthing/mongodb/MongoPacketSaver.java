package me.retrodaredevil.solarthing.mongodb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import org.bson.Document;

public class MongoPacketSaver implements PacketHandler {
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();
	
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
		collection.insertOne(Document.parse(GSON.toJson(packetCollection)));
		try {
			ClientSession session = client.startSession();
			session.commitTransaction();
		} catch(Exception e){
			throw new PacketHandleException(e);
		}
	}
}
