package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.meta.RootMetaPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.ektorp.CouchDbConnector;
import org.ektorp.DbAccessException;

public class MetaQueryHandler {
	// TODO move this into common code
	private final CouchDbConnector closedConnector;
	private final ObjectMapper objectMapper;

	public MetaQueryHandler(CouchDbConnector closedConnector, ObjectMapper objectMapper) {
		this.closedConnector = closedConnector;
		this.objectMapper = JacksonUtil.lenientSubTypeMapper(objectMapper.copy());
	}
	public RootMetaPacket query() {
		final JsonNode jsonNode;
		try {
			jsonNode = closedConnector.find(JsonNode.class, "meta");
		} catch(DbAccessException e) {
			throw new RuntimeException(e); // if we move this to a more general place such as the core module, we should make this a checked exception
		}
		// TODO create custom exception for this stuff
		if (jsonNode == null) {
			throw new RuntimeException("Could not find meta document! You should add a meta document even if you aren't going to use it so it can be cached!");
		}
		try {
			return objectMapper.treeToValue(jsonNode, RootMetaPacket.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
