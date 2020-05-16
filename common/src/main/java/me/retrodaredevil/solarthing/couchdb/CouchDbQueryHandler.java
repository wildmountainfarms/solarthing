package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import org.ektorp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CouchDbQueryHandler {
	private final Logger LOGGER = LoggerFactory.getLogger(CouchDbQueryHandler.class);

	private final CouchDbConnector connector;
	private final boolean removeQueriedPackets;

	public CouchDbQueryHandler(CouchDbConnector connector, boolean removeQueriedPackets) {
		this.connector = connector;
		this.removeQueriedPackets = removeQueriedPackets;
	}
	public List<ObjectNode> query(ViewQuery query) throws PacketHandleException{
		final ViewResult result;
		try {
			result = connector.queryView(query);
		} catch(DocumentNotFoundException e){
			throw new PacketHandleException("Document not found... Maybe the view hasn't been created in design? query: " + query, e);
		} catch(DbAccessException e){
			String message = e.getMessage();
			if (message != null && message.contains("No rows can match your key range")) {
				throw new PacketHandleException("There must be something wrong with your query. query: " + query, e);
			}
			throw new PacketHandleException("This probably means we couldn't reach the database", e);
		}
		List<ViewResult.Row> rows = result.getRows();
		List<ObjectNode> packets = new ArrayList<>(rows.size());
		for (ViewResult.Row row : rows) {
			String id = row.getId();
			ObjectNode value = (ObjectNode) row.getValueAsNode();
			String rev = value.get("_rev").asText();
			packets.add(value);
			if (removeQueriedPackets) {
				try {
					// TODO figure out a way to do a bulk remove
					connector.delete(id, rev);
				} catch (UpdateConflictException ex) {
					LOGGER.warn("Unable to remove id='" + id + "' with rev='" + rev + "'", ex);
				} catch(DbAccessException ex){
					LOGGER.warn("Unable to connect to remove id='" + id + "' with rev='" + rev + "'", ex);
				}
			}
		}
		return packets;
	}
}
