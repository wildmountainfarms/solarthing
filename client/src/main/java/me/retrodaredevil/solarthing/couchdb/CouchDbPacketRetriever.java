package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.JsonPacketReceiver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import org.ektorp.*;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CouchDbPacketRetriever implements PacketHandler {
	private final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketRetriever.class);
	
	private final JsonPacketReceiver jsonPacketReceiver;
	private final boolean removeQueriedPackets;
	private final CouchDbConnector client;

	public CouchDbPacketRetriever(CouchProperties properties, String databaseName, JsonPacketReceiver jsonPacketReceiver, boolean removeQueriedPackets){
		this.jsonPacketReceiver = jsonPacketReceiver;
		this.removeQueriedPackets = removeQueriedPackets;
		final HttpClient httpClient = EktorpUtil.createHttpClient(properties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		client = new StdCouchDbConnector(databaseName, instance);
	}
	
	protected ViewQuery alterView(ViewQuery view){
		return view;
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		ViewQuery query = alterView(new ViewQuery().designDocId("_design/packets").viewName("millis"));
		final ViewResult result;
		try {
			result = client.queryView(query);
		} catch(DocumentNotFoundException e){
			throw new PacketHandleException("Document not found... Maybe the 'millis' view hasn't been created in design 'packets'?", e);
		} catch(DbAccessException e){
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
					client.delete(id, rev);
				} catch (UpdateConflictException ex) {
					LOGGER.warn("Unable to remove id='" + id + "' with rev='" + rev + "'", ex);
				}
			}
		}
		jsonPacketReceiver.receivePackets(packets);
	}
	
}
