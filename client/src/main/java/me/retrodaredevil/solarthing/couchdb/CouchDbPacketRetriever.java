package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import java.util.List;

public class CouchDbPacketRetriever {
//	private final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketRetriever.class);

	private final CouchDbQueryHandler queryHandler;

	public CouchDbPacketRetriever(CouchProperties properties, String databaseName){
		final HttpClient httpClient = EktorpUtil.createHttpClient(properties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		this.queryHandler = new CouchDbQueryHandler(new StdCouchDbConnector(databaseName, instance));
	}
	protected ViewQuery alterView(ViewQuery view){
		return view;
	}
	public List<ObjectNode> query() throws PacketHandleException {
		ViewQuery query = alterView(SolarThingCouchDb.createMillisView());
		return queryHandler.query(query);
	}
}
