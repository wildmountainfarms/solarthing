package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;
import me.retrodaredevil.solarthing.graphql.packets.*;
import me.retrodaredevil.solarthing.misc.device.CpuTemperaturePacket;
import me.retrodaredevil.solarthing.misc.device.DevicePacket;
import me.retrodaredevil.solarthing.packets.collection.*;
import me.retrodaredevil.solarthing.packets.collection.parsing.*;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacket;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.command.packets.MateCommandFeedbackPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXOperationalModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class SolarThingGraphQLService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolarThingGraphQLService.class);

	private final DefaultInstanceOptions defaultInstanceOptions;

	private final CouchDbQueryHandler statusQueryHandler;
	private final CouchDbQueryHandler eventQueryHandler;
	private final PacketGroupParser statusParser;
	private final PacketGroupParser eventParser;

	public SolarThingGraphQLService(DefaultInstanceOptions defaultInstanceOptions, ObjectMapper originalObjectMapper, CouchProperties couchProperties) {
		this.defaultInstanceOptions = defaultInstanceOptions;
		ObjectMapper objectMapper = JacksonUtil.lenientMapper(originalObjectMapper.copy());
		statusParser = new SimplePacketGroupParser(new LenientPacketParser(
				MultiPacketConverter.createFrom(objectMapper, SolarStatusPacket.class, SolarExtraPacket.class, DevicePacket.class, InstancePacket.class)
		));
		eventParser = new SimplePacketGroupParser(new LenientPacketParser(
				MultiPacketConverter.createFrom(objectMapper, SolarEventPacket.class, MateCommandFeedbackPacket.class, MateCommandFeedbackPacket.class, InstancePacket.class)
		));

		final HttpClient httpClient = EktorpUtil.createHttpClient(couchProperties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		statusQueryHandler = new CouchDbQueryHandler(new StdCouchDbConnector(SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, instance));
		eventQueryHandler = new CouchDbQueryHandler(new StdCouchDbConnector(SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME, instance));
	}
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
		Map<String, List<InstancePacketGroup>> map = PacketGroups.parsePackets(rawPacketGroups, defaultInstanceOptions);
		if(map.containsKey(sourceId)){
			List<InstancePacketGroup> instancePacketGroupList = map.get(sourceId);
			return PacketGroups.orderByFragment(instancePacketGroupList);
		}
		throw new NoSuchElementException("No element with sourceId: '" + sourceId + "' available keys are: " + map.keySet());
	}

	@GraphQLQuery
	public SolarThingStatusQuery queryStatus(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @NotNull String sourceId){
		return new SolarThingStatusQuery(new BasicPacketGetter(queryPackets(statusQueryHandler, statusParser, from, to, sourceId), PacketFilter.KEEP_ALL));
	}
	@GraphQLQuery
	public SolarThingStatusQuery queryStatusLast(
			@GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @NotNull String sourceId, @GraphQLArgument(name = "reversed") Boolean reversed){
		List<? extends InstancePacketGroup> packets = queryPackets(statusQueryHandler, statusParser, to - 2 * 60 * 1000, to, sourceId);
		List<InstancePacketGroup> lastPackets = new ArrayList<>();
		for(List<InstancePacketGroup> packetGroups : PacketGroups.mapFragments(packets).values()) {
			lastPackets.add(packetGroups.get(packetGroups.size() - 1));
		}
		return new SolarThingStatusQuery(new ReversedPacketGetter(new BasicPacketGetter(lastPackets, PacketFilter.KEEP_ALL), Boolean.TRUE.equals(reversed)));
	}
	@GraphQLQuery
	public SolarThingEventQuery queryEvent(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @NotNull String sourceId,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false") boolean includeUnknownChangePackets
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(queryPackets(eventQueryHandler, eventParser, from, to, sourceId), new UnknownChangePacketsFilter(includeUnknownChangePackets)));
	}
	@GraphQLQuery(description = "Queries events in the specified time range while only including the specified identifier in the specified fragment")
	public SolarThingEventQuery queryEventIdentifier(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @NotNull String sourceId,
			@GraphQLArgument(name = "fragmentId") @Nullable Integer fragmentId,
			@GraphQLArgument(name = "identifier") @NotNull String identifierRepresentation,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false") boolean includeUnknownChangePackets
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(
				queryPackets(eventQueryHandler, eventParser, from, to, sourceId),
				new PacketFilterMultiplexer(Arrays.asList(new FragmentFilter(fragmentId), new IdentifierFilter(identifierRepresentation), new UnknownChangePacketsFilter(includeUnknownChangePackets)))
		));
	}
	@GraphQLQuery(description = "Queries events in the specified time range while only including the specified fragment")
	public SolarThingEventQuery queryEventFragment(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @NotNull String sourceId,
			@GraphQLArgument(name = "fragmentId") @Nullable Integer fragmentId,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false") boolean includeUnknownChangePackets
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(
				queryPackets(eventQueryHandler, eventParser, from, to, sourceId),
				new PacketFilterMultiplexer(Arrays.asList(new FragmentFilter(fragmentId), new UnknownChangePacketsFilter(includeUnknownChangePackets)))
		));
	}
	private interface PacketGetter {
		<T> @NotNull List<@NotNull PacketNode<T>> getPackets(Class<T> clazz);
	}
	private static class BasicPacketGetter implements PacketGetter {
		private final List<? extends FragmentedPacketGroup> packets;
		private final PacketFilter packetFilter;

		public BasicPacketGetter(List<? extends FragmentedPacketGroup> packets, PacketFilter packetFilter) {
			this.packets = requireNonNull(packets);
			this.packetFilter = packetFilter;
		}
		@Override
		public <T> @NotNull List<@NotNull PacketNode<T>> getPackets(Class<T> clazz) {
			return PacketUtil.convertPackets(packets, clazz, packetFilter);
		}
	}
	private static class ReversedPacketGetter implements PacketGetter {
		private final PacketGetter packetGetter;
		private final boolean reversed;

		private ReversedPacketGetter(PacketGetter packetGetter, boolean reversed) {
			this.packetGetter = packetGetter;
			this.reversed = reversed;
		}

		@Override
		public @NotNull <T> List<@NotNull PacketNode<T>> getPackets(Class<T> clazz) {
			List<PacketNode<T>> r = packetGetter.getPackets(clazz);
			if (reversed) {
				Collections.reverse(r);
			}
			return r;
		}
	}
	public static class SolarThingStatusQuery {
		private final PacketGetter packetGetter;

		public SolarThingStatusQuery(PacketGetter packetGetter) {
			this.packetGetter = packetGetter;
		}

		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<BatteryVoltage>> batteryVoltage() {
			return packetGetter.getPackets(BatteryVoltage.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXStatusPacket>> fxStatus() {
			return packetGetter.getPackets(FXStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<MXStatusPacket>> mxStatus() {
			return packetGetter.getPackets(MXStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<DailyFXPacket>> fxDaily() {
			return packetGetter.getPackets(DailyFXPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<RoverStatusPacket>> roverStatus() {
			return packetGetter.getPackets(RoverStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<CpuTemperaturePacket>> cpuTemperature() {
			return packetGetter.getPackets(CpuTemperaturePacket.class);
		}
	}
	public static class SolarThingEventQuery {
		private final PacketGetter packetGetter;

		public SolarThingEventQuery(PacketGetter packetGetter) {
			this.packetGetter = packetGetter;
		}

		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXACModeChangePacket>> fxACModeChange() {
			return packetGetter.getPackets(FXACModeChangePacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXOperationalModeChangePacket>> fxOperationalModeChange() {
			return packetGetter.getPackets(FXOperationalModeChangePacket.class);
		}

	}
}
