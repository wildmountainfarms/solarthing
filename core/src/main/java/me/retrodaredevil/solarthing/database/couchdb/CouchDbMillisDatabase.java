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
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;
import me.retrodaredevil.solarthing.database.DatabaseSource;
import me.retrodaredevil.solarthing.database.MillisDatabase;
import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParseException;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParsingErrorHandler;
import me.retrodaredevil.solarthing.packets.collection.parsing.SimplePacketGroupParser;

import java.util.ArrayList;
import java.util.List;

public class CouchDbMillisDatabase implements MillisDatabase {

	private final CouchDbDatabase database;
	private final ObjectMapper mapper;
	private final SimplePacketGroupParser parser;
	private final CouchDbDatabaseSource databaseSource;

	public CouchDbMillisDatabase(CouchDbDatabase database, ObjectMapper mapper, PacketParsingErrorHandler errorHandler) {
		this.database = database;
		this.mapper = mapper;
		this.parser = new SimplePacketGroupParser(mapper, errorHandler);
		databaseSource = new CouchDbDatabaseSource(database);
	}

	private VersionedPacket<StoredPacketGroup> jsonDataToStoredPacketGroup(JsonData jsonData) throws SolarThingDatabaseException {
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
		String documentId = objectNode.get("_id").asText();
		String documentRevision = objectNode.get("_rev").asText();
		StoredPacketGroup storedPacketGroup = PacketGroups.createStoredPacketGroup(packetGroup, new CouchDbStoredIdentifier(packetGroup.getDateMillis(), documentId, documentRevision));
		return new VersionedPacket<>(storedPacketGroup, new RevisionUpdateToken(documentRevision));
	}

	@Override
	public List<StoredPacketGroup> query(MillisQuery query) throws SolarThingDatabaseException {
		final ViewResponse response;
		try {
			response = database.queryView(SolarThingCouchDb.createMillisNullView(CouchDbQueryUtil.createMillisNullParams(query)));
		} catch (CouchDbException e) {
			throw ExceptionUtil.createFromCouchDbException(e);
		}
		List<ViewResponse.DocumentEntry> rows = response.getRows();
		List<StoredPacketGroup> r = new ArrayList<>(rows.size());
		for (ViewResponse.DocumentEntry row : rows) {
			JsonData jsonData = row.getDoc(); // When using includeDocs=true, we want to use the doc, not its value (which is null with millisNull)
			StoredPacketGroup storedPacketGroup = jsonDataToStoredPacketGroup(jsonData).getPacket();
			r.add(storedPacketGroup);
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
				RevisionUpdateToken revisionUpdateToken = CouchDbSolarThingDatabase.checkUpdateToken(updateToken);
				response = database.updateDocument(packetCollection.getDbId(), revisionUpdateToken.getRevision(), jsonData);
			}
			return new RevisionUpdateToken(response.getRev());
		} catch (CouchDbException e) {
			throw ExceptionUtil.createFromCouchDbException(e);
		}
	}

	@Override
	public VersionedPacket<StoredPacketGroup> getPacketCollection(String documentId) throws SolarThingDatabaseException {
		final DocumentData documentData;
		try {
			documentData = database.getDocument(documentId);
		} catch (CouchDbException e) {
			throw ExceptionUtil.createFromCouchDbException("Could not get packet collection with document ID: " + documentId, e);
		}
		JsonData jsonData = documentData.getJsonData();
		return jsonDataToStoredPacketGroup(jsonData);
	}

	@Deprecated
	@Override
	public UpdateToken getCurrentUpdateToken(String documentId) throws SolarThingDatabaseException {
		try {
			return new RevisionUpdateToken(database.getCurrentRevision(documentId));
		} catch (CouchDbException e) {
			throw ExceptionUtil.createFromCouchDbException("Couldn't get revision", e);
		}
	}

	@Override
	public DatabaseSource getDatabaseSource() {
		return databaseSource;
	}

	@Override
	public String toString() {
		return "CouchDbMillisDatabase(database name=" + database.getName() + ")";
	}
}
