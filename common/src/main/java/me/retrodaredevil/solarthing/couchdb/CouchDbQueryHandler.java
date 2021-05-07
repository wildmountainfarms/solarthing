package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.ViewQuery;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotFoundException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CouchDbQueryHandler {
//	private final Logger LOGGER = LoggerFactory.getLogger(CouchDbQueryHandler.class);

	private final CouchDbDatabase database;

	public CouchDbQueryHandler(CouchDbDatabase database) {
		requireNonNull(this.database = database);
	}

	public List<ObjectNode> query(ViewQuery query) throws PacketHandleException{
		final ViewResponse response;
		try {
			response = database.queryView(query);
		} catch(CouchDbNotFoundException e){
			String reason = "";
			String error = "";
			if (e.getErrorResponse() != null) {
				reason = e.getErrorResponse().getReason();
				error = e.getErrorResponse().getError();
			}
			throw new PacketHandleException("Document not found... Maybe the view hasn't been created in design? query: " + query + ". On database: " + database.getName() + " reason: " + reason + " " + error, e);
		} catch(CouchDbException e){
			String message = e.getMessage();
			if (message != null && message.contains("No rows can match your key range")) {
				throw new PacketHandleException("There must be something wrong with your query. query: " + query, e);
			}
			throw new PacketHandleException("This probably means we couldn't reach the database", e);
		}
		List<ViewResponse.DocumentEntry> rows = response.getRows();
		List<ObjectNode> packets = new ArrayList<>(rows.size());
		for (ViewResponse.DocumentEntry row : rows) {
			JsonData value = row.getValue();
			final JsonNode jsonNode;
			try {
				jsonNode = CouchDbJacksonUtil.getNodeFrom(value);
			} catch (JsonProcessingException e) {
				throw new PacketHandleException("Couldn't parse JSON data!", e);
			}
			if (!(jsonNode instanceof ObjectNode)) {
				throw new IllegalStateException("One of the values is not an ObjectNode! This probably means that your view isn't correct. Please report this bug.");
			}
			ObjectNode node = (ObjectNode) jsonNode;
			packets.add(node);
		}
		return packets;
	}
}
