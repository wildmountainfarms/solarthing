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
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParseException;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParsingErrorHandler;
import me.retrodaredevil.solarthing.packets.collection.parsing.SimplePacketGroupParser;

import java.util.ArrayList;
import java.util.List;

public class CouchDbMillisDatabase implements MillisDatabase {

	private final CouchDbDatabase database;
	private final ObjectMapper mapper;
	private final SimplePacketGroupParser parser;

	public CouchDbMillisDatabase(CouchDbDatabase database, ObjectMapper mapper, PacketParsingErrorHandler errorHandler) {
		this.database = database;
		this.mapper = mapper;
		this.parser = new SimplePacketGroupParser(mapper, errorHandler);
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
			final PacketGroup packetGroup;
			try {
				packetGroup = parser.parse(objectNode);
			} catch (PacketParseException e) {
				throw new SolarThingDatabaseException(e);
			}
			r.add(packetGroup);
		}
		return r;
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
