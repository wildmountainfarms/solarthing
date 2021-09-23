package me.retrodaredevil.solarthing.type.closed.meta.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotFoundException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.solarthing.type.closed.meta.RootMetaPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;

/**
 * Handles the querying of the meta document from the solarthing_closed database. ({@link me.retrodaredevil.solarthing.SolarThingConstants#CLOSED_DATABASE})
 */
@Deprecated
public class MetaQueryHandler {
	private final CouchDbDatabase database;
	private final ObjectMapper objectMapper;

	public MetaQueryHandler(CouchDbDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = JacksonUtil.lenientSubTypeMapper(objectMapper.copy());
	}

	/**
	 * @return The {@link RootMetaPacket}
	 * @throws MetaException Thrown if database could not be accessed, if the meta document does not exist, or if this failed to parse the meta document correctly
	 */
	public RootMetaPacket query() throws MetaException {
		final DocumentData data;
		try {
			data = database.getDocument("meta");
		} catch(CouchDbNotFoundException e){
			throw new MetaException("Could not find meta document! You should add a meta document even if you aren't going to use it so it can be cached!");
		} catch (CouchDbException e) {
			throw new MetaException(e);
		}
		JsonData jsonData = data.getJsonData();
		try {
			return CouchDbJacksonUtil.readValue(objectMapper, jsonData, RootMetaPacket.class);
		} catch (JsonProcessingException e) {
			throw new MetaException("Invalid meta! Failed to parse!", e);
		}
	}
}
