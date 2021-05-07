package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.ViewQueryParamsBuilder;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;
import me.retrodaredevil.solarthing.meta.*;
import me.retrodaredevil.solarthing.meta.query.MetaException;
import me.retrodaredevil.solarthing.meta.query.MetaQueryHandler;
import me.retrodaredevil.solarthing.misc.common.meta.DataMetaPacket;
import me.retrodaredevil.solarthing.misc.device.DevicePacket;
import me.retrodaredevil.solarthing.misc.weather.WeatherPacket;
import me.retrodaredevil.solarthing.packets.collection.*;
import me.retrodaredevil.solarthing.packets.collection.parsing.*;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacket;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.command.packets.MateCommandFeedbackPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingSettingsPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingTemperatureAdjustPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SimpleQueryHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleQueryHandler.class);

	private final DefaultInstanceOptions defaultInstanceOptions;

	private final CouchDbQueryHandler statusQueryHandler;
	private final CouchDbQueryHandler eventQueryHandler;
	private final MetaQueryHandler metaQueryHandler;
	private final PacketGroupParser statusParser;
	private final PacketGroupParser eventParser;
	public SimpleQueryHandler(DefaultInstanceOptions defaultInstanceOptions, ObjectMapper originalObjectMapper, CouchDbDatabaseSettings couchDbDatabaseSettings) {
		this.defaultInstanceOptions = defaultInstanceOptions;
		ObjectMapper objectMapper = JacksonUtil.lenientMapper(originalObjectMapper.copy());
		statusParser = new SimplePacketGroupParser(new LenientPacketParser(
				MultiPacketConverter.createFrom(objectMapper, SolarStatusPacket.class, SolarExtraPacket.class, DevicePacket.class, WeatherPacket.class, InstancePacket.class)
		));
		eventParser = new SimplePacketGroupParser(new LenientPacketParser(
				MultiPacketConverter.createFrom(objectMapper, SolarEventPacket.class, MateCommandFeedbackPacket.class, InstancePacket.class)
		));

		CouchDbInstance instance = CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties());
		statusQueryHandler = new CouchDbQueryHandler(instance.getDatabase(SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME));
		eventQueryHandler = new CouchDbQueryHandler(instance.getDatabase(SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME));
		ObjectMapper metaObjectMapper = objectMapper.copy();
		metaObjectMapper.getSubtypeResolver().registerSubtypes(TargetMetaPacket.class, DeviceInfoPacket.class, DataMetaPacket.class, FXChargingSettingsPacket.class, FXChargingTemperatureAdjustPacket.class);
		metaQueryHandler = new MetaQueryHandler(instance.getDatabase(SolarThingConstants.CLOSED_UNIQUE_NAME), metaObjectMapper);
	}

	/**
	 * Converts a list of {@link InstancePacketGroup}s to merged {@link FragmentedPacketGroup}s.
	 */
	public List<? extends FragmentedPacketGroup> sortPackets(List<? extends InstancePacketGroup> packets, String sourceId) {
		if (packets.isEmpty()) {
			return Collections.emptyList();
		}
		if (sourceId == null) {
			return PacketGroups.mergePackets(PacketGroups.parseToInstancePacketGroups(packets, defaultInstanceOptions), 2 * 60 * 1000, 2 * 60 * 1000L);
		}
		return PacketGroups.sortPackets(packets, defaultInstanceOptions, 2 * 60 * 1000, 2 * 60 * 1000L).getOrDefault(sourceId, Collections.emptyList());
	}
	/**
	 *
	 * @param queryHandler The CouchDB query handler
	 * @param parser The parser
	 * @param from The date millis from
	 * @param to The date millis to
	 * @param sourceId The source ID or null. If null, the returned List may contain packet groups from multiple sources
	 * @return The resulting packets
	 */
	private List<? extends InstancePacketGroup> queryPackets(CouchDbQueryHandler queryHandler, PacketGroupParser parser, long from, long to, String sourceId) {
		final List<ObjectNode> packets;
		try {
			packets = queryHandler.query(SolarThingCouchDb.createMillisView(new ViewQueryParamsBuilder()
					.startKey(from)
					.endKey(to)
					.build()));
			// TODO when we used Ektorp, it gave us a cacheOk option. We need to figure out how this ties to our new CouchDB API
		} catch (PacketHandleException e) {
			throw new RuntimeException(e);
		}
		if(packets.isEmpty()){
			System.out.println("No packets were queried between " + from + " and " + to);
			return Collections.emptyList();
		}
		List<PacketGroup> rawPacketGroups = new ArrayList<>();
		for(ObjectNode node : packets) {
			try {
				PacketGroup group = parser.parse(node);
				rawPacketGroups.add(group);
			} catch (PacketParseException e) {
				LOGGER.error("Error parsing packet group. We will continue.", e);
			}
		}
		if (sourceId == null) {
			return PacketGroups.parseToInstancePacketGroups(rawPacketGroups, defaultInstanceOptions);
		}
		Map<String, List<InstancePacketGroup>> map = PacketGroups.parsePackets(rawPacketGroups, defaultInstanceOptions);
		if(map.containsKey(sourceId)){
			List<InstancePacketGroup> instancePacketGroupList = map.get(sourceId);
			return PacketGroups.orderByFragment(instancePacketGroupList);
		}
		throw new NoSuchElementException("No element with sourceId: '" + sourceId + "' available keys are: " + map.keySet());
	}
	public List<? extends InstancePacketGroup> queryStatus(long from, long to, String sourceId) {
		return queryPackets(statusQueryHandler, statusParser, from, to, sourceId);
	}
	public List<? extends InstancePacketGroup> queryEvent(long from, long to, String sourceId) {
		return queryPackets(eventQueryHandler, eventParser, from, to, sourceId);
	}

	public MetaDatabase queryMeta() {
		try {
			return new DefaultMetaDatabase(metaQueryHandler.query());
		} catch(MetaException e) {
			return EmptyMetaDatabase.getInstance();
		}
	}

	public DefaultInstanceOptions getDefaultInstanceOptions() {
		return defaultInstanceOptions;
	}
}
