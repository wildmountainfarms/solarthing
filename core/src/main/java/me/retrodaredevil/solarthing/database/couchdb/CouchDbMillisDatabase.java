package me.retrodaredevil.solarthing.database.couchdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import me.retrodaredevil.solarthing.database.MillisDatabase;
import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;

import java.util.ArrayList;
import java.util.List;

public class CouchDbMillisDatabase implements MillisDatabase {

	private final CouchDbDatabase database;
	private final ObjectMapper mapper;
	private final PacketParsingErrorHandler errorHandler;

	public CouchDbMillisDatabase(CouchDbDatabase database, ObjectMapper mapper, PacketParsingErrorHandler errorHandler) {
		this.database = database;
		this.mapper = mapper;
		this.errorHandler = errorHandler;
	}

	@Override
	public List<PacketGroup> query(MillisQuery query) throws SolarThingDatabaseException {
		final ViewResponse response;
		try {
			response = database.queryView("packets", "millis", CouchDbQueryUtil.createParamsFrom(query));
		} catch (CouchDbException e) {
			throw new SolarThingDatabaseException(e);
		}
		List<ViewResponse.DocumentEntry> rows = response.getRows();
		List<PacketGroup> r = new ArrayList<>(rows.size());
		for (ViewResponse.DocumentEntry row : rows) {
			JsonData jsonData = row.getValue();
			final JsonNode jsonNode;
			try {
				jsonNode = CouchDbJacksonUtil.getNodeFrom(jsonData);
			} catch (JsonProcessingException e) {
				throw new SolarThingDatabaseException("We couldn't parse some of the data into JSON. This should never happen", e);
			}
			if (!jsonNode.isObject()) {
				throw new SolarThingDatabaseException("Something must be wrong with the packet millis view because we got this jsonNode: " + jsonNode);
			}
			ObjectNode objectNode = (ObjectNode) jsonNode;
			r.add(parsePacketGroup(objectNode));
		}
		return r;
	}
	private PacketGroup parsePacketGroup(ObjectNode objectNode) throws SolarThingDatabaseException {
		JsonNode dateMillisNode = objectNode.get("dateMillis");
		if(dateMillisNode == null){
			throw new SolarThingDatabaseException("'dateMillis' does not exist for objectNode=" + objectNode);
		}
		if(!dateMillisNode.isNumber()){
			throw new SolarThingDatabaseException("'dateMillis' is not a number! dateMillisNode=" + dateMillisNode);
		}
		long dateMillis = dateMillisNode.asLong();
		JsonNode packetsNode = objectNode.get("packets");
		if(packetsNode == null){
			throw new SolarThingDatabaseException("'packets' does not exist for objectNode=" + objectNode);
		}
		if(!packetsNode.isArray()){
			throw new SolarThingDatabaseException("'packets' is not an array! packetsNode=" + packetsNode);
		}
		List<Packet> packetList = new ArrayList<>();
		for (JsonNode jsonPacket : packetsNode) {
			DocumentedPacket packet = null;
			try {
				packet = mapper.convertValue(jsonPacket, DocumentedPacket.class);
			} catch (IllegalArgumentException ex) {
				errorHandler.handleError(ex);
			}
			if(packet != null){
				packetList.add(packet);
			}
		}

		return PacketGroups.createPacketGroup(packetList, dateMillis);
	}

	@Override
	public UpdateToken uploadPacketCollection(PacketCollection packetCollection, UpdateToken updateToken) throws SolarThingDatabaseException {
		final JsonData jsonData;
		try {
			jsonData = new StringJsonData(mapper.writeValueAsString(packetCollection));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Couldn't serialize the packet collection", e);
		}
		try {
			final DocumentResponse response;
			if (updateToken == null) {
				response = database.putDocument(packetCollection.getDbId(), jsonData);
			} else {
				RevisionUpdateToken revisionUpdateToken = (RevisionUpdateToken) updateToken;
				response = database.updateDocument(packetCollection.getDbId(), revisionUpdateToken.getRevision(), jsonData);
			}
			return new RevisionUpdateToken(response.getRev());
		} catch (CouchDbException e) {
			throw new SolarThingDatabaseException(e);
		}
	}

	@Override
	public UpdateToken getCurrentUpdateToken(String documentId) throws SolarThingDatabaseException {
		try {
			return new RevisionUpdateToken(database.getCurrentRevision(documentId));
		} catch (CouchDbException e) {
			throw new SolarThingDatabaseException("Couldn't get revision", e);
		}
	}
}
