package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.ViewQueryParams;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Helps query packets using the default millis view
 */
@Deprecated
public class CouchDbPacketRetriever {
//	private final Logger LOGGER = LoggerFactory.getLogger(CouchDbPacketRetriever.class);

	private final CouchDbQueryHandler queryHandler;
	private final ViewQueryParamsCreator paramsCreator;

	public CouchDbPacketRetriever(CouchDbDatabase database, ViewQueryParamsCreator viewQueryParamsCreator){
		this.queryHandler = new CouchDbQueryHandler(database);
		requireNonNull(this.paramsCreator = viewQueryParamsCreator);
	}
	public List<ObjectNode> query() throws PacketHandleException {
		return queryHandler.query(SolarThingCouchDb.createMillisView(paramsCreator.create()));
	}
	public interface ViewQueryParamsCreator {
		ViewQueryParams create();
	}
}
