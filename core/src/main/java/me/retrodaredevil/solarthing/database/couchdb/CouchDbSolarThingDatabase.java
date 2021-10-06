package me.retrodaredevil.solarthing.database.couchdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotModifiedException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.type.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.commands.packets.status.CommandStatusPacket;
import me.retrodaredevil.solarthing.database.MillisDatabase;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.type.event.feedback.FeedbackPacket;
import me.retrodaredevil.solarthing.type.closed.meta.DeviceInfoPacket;
import me.retrodaredevil.solarthing.type.closed.meta.RootMetaPacket;
import me.retrodaredevil.solarthing.type.closed.meta.TargetMetaPacket;
import me.retrodaredevil.solarthing.misc.common.meta.DataMetaPacket;
import me.retrodaredevil.solarthing.misc.device.DevicePacket;
import me.retrodaredevil.solarthing.misc.error.ErrorPacket;
import me.retrodaredevil.solarthing.misc.weather.WeatherPacket;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParsingErrorHandler;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.packets.security.SecurityPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacket;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.command.packets.MateCommandFeedbackPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingSettingsPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingTemperatureAdjustPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jetbrains.annotations.NotNull;

public class CouchDbSolarThingDatabase implements SolarThingDatabase {

	private final CouchDbDatabase closedDatabase;
	private final ObjectMapper metaObjectMapper;
	private final ObjectMapper simpleObjectMapper;

	private final CouchDbMillisDatabase statusDatabase;
	private final CouchDbMillisDatabase eventDatabase;
	private final CouchDbMillisDatabase openDatabase;

	private final CouchDbAlterDatabase alterDatabase;

	/**
	 *
	 * @param instance
	 * @param errorHandler
	 * @param mapper The object mapper. "Lenient" settings should have already been applied to this
	 */
	public CouchDbSolarThingDatabase(CouchDbInstance instance, PacketParsingErrorHandler errorHandler, ObjectMapper mapper) {
		closedDatabase = instance.getDatabase(SolarThingConstants.CLOSED_DATABASE);
		metaObjectMapper = JacksonUtil.lenientSubTypeMapper(mapper.copy());
		metaObjectMapper.getSubtypeResolver().registerSubtypes(TargetMetaPacket.class, DeviceInfoPacket.class, DataMetaPacket.class, FXChargingSettingsPacket.class, FXChargingTemperatureAdjustPacket.class);
		simpleObjectMapper = mapper.copy();

		ObjectMapper statusMapper = mapper.copy();
		statusMapper.getSubtypeResolver().registerSubtypes(SolarStatusPacket.class, SolarExtraPacket.class, DevicePacket.class, ErrorPacket.class, WeatherPacket.class, InstancePacket.class, CommandStatusPacket.class);
		statusDatabase = new CouchDbMillisDatabase(instance.getDatabase(SolarThingConstants.STATUS_DATABASE), statusMapper, errorHandler);

		ObjectMapper eventMapper = mapper.copy();
		eventMapper.getSubtypeResolver().registerSubtypes(SolarEventPacket.class, MateCommandFeedbackPacket.class, FeedbackPacket.class, InstancePacket.class, InstancePacket.class);
		eventDatabase = new CouchDbMillisDatabase(instance.getDatabase(SolarThingConstants.EVENT_DATABASE), eventMapper, errorHandler);

		ObjectMapper openMapper = mapper.copy();
		openMapper.getSubtypeResolver().registerSubtypes(SecurityPacket.class, InstancePacket.class);
		openDatabase = new CouchDbMillisDatabase(instance.getDatabase(SolarThingConstants.OPEN_DATABASE), openMapper, errorHandler);

		alterDatabase = new CouchDbAlterDatabase(instance.getDatabase(SolarThingConstants.ALTER_DATABASE), mapper); // we don't need to register any subtypes for the alter database because it's just that cool :)
	}
	public static CouchDbSolarThingDatabase create(CouchDbInstance instance) {
		return new CouchDbSolarThingDatabase(instance, PacketParsingErrorHandler.DO_NOTHING, JacksonUtil.lenientMapper(JacksonUtil.defaultMapper()));
	}


	@Override
	public @NotNull MillisDatabase getStatusDatabase() { return statusDatabase; }
	@Override
	public @NotNull MillisDatabase getEventDatabase() { return eventDatabase; }
	@Override
	public @NotNull MillisDatabase getOpenDatabase() { return openDatabase; }

	@Override
	public @NotNull CouchDbAlterDatabase getAlterDatabase() {
		return alterDatabase;
	}

	@Override
	public @Nullable VersionedPacket<RootMetaPacket> queryMetadata(UpdateToken updateToken) throws SolarThingDatabaseException {
		DocumentData data = queryDocument(closedDatabase, RootMetaPacket.DOCUMENT_ID, updateToken);
		if (data == null) {
			return null;
		}
		JsonData jsonData = data.getJsonData();
		try {
			RootMetaPacket packet = CouchDbJacksonUtil.readValue(metaObjectMapper, jsonData, RootMetaPacket.class);
			return new VersionedPacket<>(packet, new RevisionUpdateToken(data.getRevision()));
		} catch (JsonProcessingException e) {
			throw new SolarThingDatabaseException("Invalid meta! Failed to parse!", e);
		}
	}

	@Override
	public @Nullable VersionedPacket<AuthorizationPacket> queryAuthorized(UpdateToken updateToken) throws SolarThingDatabaseException {
		DocumentData data = queryDocument(closedDatabase, AuthorizationPacket.DOCUMENT_ID, updateToken);
		if (data == null) {
			return null;
		}
		JsonData jsonData = data.getJsonData();
		try {
			AuthorizationPacket packet = CouchDbJacksonUtil.readValue(simpleObjectMapper, jsonData, AuthorizationPacket.class);
			return new VersionedPacket<>(packet, new RevisionUpdateToken(data.getRevision()));
		} catch (JsonProcessingException e) {
			throw new SolarThingDatabaseException("Invalid authorization packet! Failed to parse!", e);
		}
	}

	private static DocumentData queryDocument(CouchDbDatabase database, String name, UpdateToken updateToken) throws SolarThingDatabaseException {
		final String revision;
		if (updateToken == null) {
			revision = null;
		} else {
			revision = ((RevisionUpdateToken) updateToken).getRevision();
		}
		try {
			return database.getDocumentIfUpdated(name, revision);
		} catch (CouchDbNotModifiedException e) {
			return null;
		} catch (CouchDbException e) {
			throw new SolarThingDatabaseException(e);
		}
	}

}
