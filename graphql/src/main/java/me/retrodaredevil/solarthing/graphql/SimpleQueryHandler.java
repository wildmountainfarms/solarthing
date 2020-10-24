package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;
import me.retrodaredevil.solarthing.meta.DefaultMetaDatabase;
import me.retrodaredevil.solarthing.meta.DeviceInfoPacket;
import me.retrodaredevil.solarthing.meta.MetaDatabase;
import me.retrodaredevil.solarthing.meta.TargetMetaPacket;
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
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingSettings;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingSettingsPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingTemperatureAdjustPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
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
	public SimpleQueryHandler(DefaultInstanceOptions defaultInstanceOptions, ObjectMapper originalObjectMapper, CouchProperties couchProperties) {
		this.defaultInstanceOptions = defaultInstanceOptions;
		ObjectMapper objectMapper = JacksonUtil.lenientMapper(originalObjectMapper.copy());
		statusParser = new SimplePacketGroupParser(new LenientPacketParser(
				MultiPacketConverter.createFrom(objectMapper, SolarStatusPacket.class, SolarExtraPacket.class, DevicePacket.class, WeatherPacket.class, InstancePacket.class)
		));
		eventParser = new SimplePacketGroupParser(new LenientPacketParser(
				MultiPacketConverter.createFrom(objectMapper, SolarEventPacket.class, MateCommandFeedbackPacket.class, InstancePacket.class)
		));

		final HttpClient httpClient = EktorpUtil.createHttpClient(couchProperties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		statusQueryHandler = new CouchDbQueryHandler(new StdCouchDbConnector(SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, instance));
		eventQueryHandler = new CouchDbQueryHandler(new StdCouchDbConnector(SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME, instance));
		ObjectMapper metaObjectMapper = objectMapper.copy();
		metaObjectMapper.getSubtypeResolver().registerSubtypes(TargetMetaPacket.class, DeviceInfoPacket.class, DataMetaPacket.class, FXChargingSettingsPacket.class, FXChargingTemperatureAdjustPacket.class);
		metaQueryHandler = new MetaQueryHandler(new StdCouchDbConnector(SolarThingConstants.CLOSED_UNIQUE_NAME, instance), metaObjectMapper);
	}
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
	 * @return
	 */
	private List<? extends InstancePacketGroup> queryPackets(CouchDbQueryHandler queryHandler, PacketGroupParser parser, long from, long to, String sourceId) {
		final List<ObjectNode> packets;
		try {
			packets = queryHandler.query(SolarThingCouchDb.createMillisView()
					.startKey(from)
					.endKey(to)
					.cacheOk(true));
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
		return new DefaultMetaDatabase(metaQueryHandler.query());
	}

	public DefaultInstanceOptions getDefaultInstanceOptions() {
		return defaultInstanceOptions;
	}
}
