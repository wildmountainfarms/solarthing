package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.graphql.packets.PacketNode;
import me.retrodaredevil.solarthing.graphql.packets.PacketUtil;
import me.retrodaredevil.solarthing.misc.device.CpuTemperaturePacket;
import me.retrodaredevil.solarthing.misc.device.DevicePacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.parsing.*;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.extra.DailyMXPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class SolarThingGraphQLService {
	private final Logger LOGGER = LoggerFactory.getLogger(SolarThingGraphQLService.class);

	private final CouchDbQueryHandler queryHandler;
	private final PacketGroupParser statusParser;
	public SolarThingGraphQLService(ObjectMapper originalObjectMapper, CouchProperties couchProperties) {
		ObjectMapper objectMapper = JacksonUtil.lenientMapper(originalObjectMapper.copy());
		statusParser = new SimplePacketGroupParser(new PacketParserMultiplexer(Arrays.asList(
				new ObjectMapperPacketConverter(objectMapper, SolarStatusPacket.class),
				new ObjectMapperPacketConverter(objectMapper, SolarExtraPacket.class),
				new ObjectMapperPacketConverter(objectMapper, DevicePacket.class),
				new ObjectMapperPacketConverter(objectMapper, InstancePacket.class)
		), PacketParserMultiplexer.LenientType.FULLY_LENIENT));

		final HttpClient httpClient = EktorpUtil.createHttpClient(couchProperties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		queryHandler = new CouchDbQueryHandler(new StdCouchDbConnector(SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, instance), false);
	}
	private List<? extends FragmentedPacketGroup> queryPackets(long from, long to, String sourceId) {
		final List<ObjectNode> packets;
		try {
			packets = queryHandler.query(new ViewQuery()
					.designDocId("_design/packets")
					.viewName("millis")
					.startKey(from)
					.endKey(to)
					.cacheOk(true));
		} catch (PacketHandleException e) {
			throw new RuntimeException(e);
		}
		if(packets.isEmpty()){
			throw new NoSuchElementException("No packets were queried between " + from + " and " + to);
		}
		List<PacketGroup> rawPacketGroups = new ArrayList<>();
		for(ObjectNode node : packets) {
			try {
				PacketGroup group = statusParser.parse(node);
				rawPacketGroups.add(group);
			} catch (PacketParseException e) {
				LOGGER.error("Error parsing packet group. We will continue.", e);
			}
		}
		Map<String, List<InstancePacketGroup>> map = PacketGroups.parsePackets(rawPacketGroups);
		if(map.containsKey(sourceId)){
			List<InstancePacketGroup> instancePacketGroupList = map.get(sourceId);
			Map<Integer, List<InstancePacketGroup>> mappedPackets = PacketGroups.mapFragments(instancePacketGroupList);
			Collection<Integer> sortedFragmentKeys = new TreeSet<>(PacketGroups.DEFAULT_FRAGMENT_ID_COMPARATOR);
			sortedFragmentKeys.addAll(mappedPackets.keySet());
			List<InstancePacketGroup> r = new ArrayList<>();
			for(Integer fragmentId : sortedFragmentKeys) {
				r.addAll(mappedPackets.get(fragmentId));
			}
			return r;
		}
		throw new NoSuchElementException("No element with sourceId: '" + sourceId + "' available keys are: " + map.keySet());
	}

	@GraphQLQuery
	public SolarThingQuery queryAll(@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to, @GraphQLArgument(name = "sourceId") String sourceId){
		return new SolarThingQuery(queryPackets(from, to, sourceId));
	}
	public static class SolarThingQuery {
		private final List<? extends FragmentedPacketGroup> packets;

		public SolarThingQuery(List<? extends FragmentedPacketGroup> packets) {
			this.packets = requireNonNull(packets);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<BatteryVoltage>> batteryVoltage() {
			return PacketUtil.convertPackets(packets, BatteryVoltage.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXStatusPacket>> fxStatus() {
			return PacketUtil.convertPackets(packets, FXStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<MXStatusPacket>> mxStatus() {
			return PacketUtil.convertPackets(packets, MXStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<DailyMXPacket>> mxDaily() {
			return PacketUtil.convertPackets(packets, DailyMXPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<DailyFXPacket>> fxDaily() {
			return PacketUtil.convertPackets(packets, DailyFXPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<CpuTemperaturePacket>> cpuTemperature() {
			return PacketUtil.convertPackets(packets, CpuTemperaturePacket.class);
		}
		//	public List<InstancePacketGroup> queryInstance(long from, long to, @NotNull String sourceId, @NotNull Integer fragmentId) { throw new UnsupportedOperationException(); }
		public List<InstancePacketGroup> queryUnsorted(String sourceId) { throw new UnsupportedOperationException(); }
		public List<FragmentedPacketGroup> querySorted(String sourceId) { throw new UnsupportedOperationException(); }

	}
}
