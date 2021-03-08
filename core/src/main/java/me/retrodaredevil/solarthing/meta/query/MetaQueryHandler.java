package me.retrodaredevil.solarthing.meta.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.meta.RootMetaPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.ektorp.CouchDbConnector;
import org.ektorp.DbAccessException;

/**
 * Handles the querying of the meta document from the solarthing_closed database. ({@link me.retrodaredevil.solarthing.SolarThingConstants#CLOSED_UNIQUE_NAME})
 */
public class MetaQueryHandler {
	private final CouchDbConnector closedConnector;
	private final ObjectMapper objectMapper;

	public MetaQueryHandler(CouchDbConnector closedConnector, ObjectMapper objectMapper) {
		this.closedConnector = closedConnector;
		this.objectMapper = JacksonUtil.lenientSubTypeMapper(objectMapper.copy());
	}

	/**
	 * @return The {@link RootMetaPacket}
	 * @throws MetaException Thrown if database could not be accessed, if the meta document does not exist, or if this failed to parse the meta document correctly
	 */
	public RootMetaPacket query() throws MetaException {
		final JsonNode jsonNode;
		try {
			jsonNode = closedConnector.find(JsonNode.class, "meta");
		} catch(DbAccessException e) {
			throw new MetaException("Could not access database", e);
		}
		if (jsonNode == null) {
			throw new MetaException("Could not find meta document! You should add a meta document even if you aren't going to use it so it can be cached!");
		}
		try {
			return objectMapper.treeToValue(jsonNode, RootMetaPacket.class);
		} catch (JsonProcessingException e) {
			throw new MetaException("Invalid meta! Failed to parse!", e);
		}
	}
}
