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
import me.retrodaredevil.couchdbjava.request.ViewQueryParamsBuilder;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.AlterDatabase;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;

import java.util.ArrayList;
import java.util.List;

public class CouchDbAlterDatabase implements AlterDatabase {
	private final CouchDbDatabase database;
	private final ObjectMapper mapper;

	public CouchDbAlterDatabase(CouchDbDatabase database, ObjectMapper mapper) {
		this.database = database;
		this.mapper = mapper;
	}

	@Override
	public @NotNull UpdateToken upload(StoredAlterPacket storedAlterPacket) throws SolarThingDatabaseException {
		final JsonData jsonData;
		try {
			jsonData = new StringJsonData(mapper.writeValueAsString(storedAlterPacket));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Couldn't serialize the packet collection", e);
		}
		try {
			DocumentResponse response = database.putDocument(storedAlterPacket.getDbId(), jsonData);
			return new RevisionUpdateToken(response.getRev());
		} catch (CouchDbException e) {
			throw new SolarThingDatabaseException(e);
		}
	}

	@Override
	public @NotNull List<VersionedPacket<StoredAlterPacket>> queryAll(String sourceId) throws SolarThingDatabaseException {
		final ViewResponse allDocs;
		try {
			allDocs = database.allDocs(new ViewQueryParamsBuilder().build());
		} catch (CouchDbException e) {
			throw new SolarThingDatabaseException("Could not query", e);
		}

		List<ViewResponse.DocumentEntry> rows = allDocs.getRows();
		List<VersionedPacket<StoredAlterPacket>> r = new ArrayList<>(rows.size());
		for (ViewResponse.DocumentEntry row : rows) {
			if (row.getId().startsWith("_")) { // ignore design documents
				continue;
			}
			JsonData jsonData = row.getValue();
			final JsonNode jsonNode;
			try {
				jsonNode = CouchDbJacksonUtil.getNodeFrom(jsonData);
			} catch (JsonProcessingException e) {
				throw new SolarThingDatabaseException("We couldn't parse some of the data into JSON. This should never happen", e);
			}
			if (!jsonNode.isObject()) {
				throw new SolarThingDatabaseException("Something must be wrong with _all_docs!");
			}
			ObjectNode objectNode = (ObjectNode) jsonNode;
			final StoredAlterPacket storedAlterPacket;
			try {
				storedAlterPacket = mapper.treeToValue(objectNode, StoredAlterPacket.class);;
			} catch (JsonProcessingException e) {
				throw new SolarThingDatabaseException("Could not parse", e);
			}
//			String documentId = objectNode.get("_id").asText();
			String documentRevision = objectNode.get("_rev").asText();
			VersionedPacket<StoredAlterPacket> versionedPacket = new VersionedPacket<>(storedAlterPacket, new RevisionUpdateToken(documentRevision));
			r.add(versionedPacket);
		}
		return r;

	}

	@Override
	public void delete(String documentId, UpdateToken updateToken) throws SolarThingDatabaseException {
		RevisionUpdateToken revisionUpdateToken = (RevisionUpdateToken) updateToken;
		String revision = revisionUpdateToken.getRevision();
		try {
			database.deleteDocument(documentId, revision);
		} catch (CouchDbException e) {
			throw new SolarThingDatabaseException("Could not delete documentId: " + documentId + " revision: " + revision, e);
		}
	}
}
