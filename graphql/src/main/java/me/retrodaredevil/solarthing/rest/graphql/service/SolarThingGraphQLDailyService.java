package me.retrodaredevil.solarthing.rest.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.DataNode;
import me.retrodaredevil.solarthing.rest.graphql.packets.PacketFilter;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.SimpleNode;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationCalc;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationConfig;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationPair;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

import static me.retrodaredevil.solarthing.rest.graphql.service.SchemaConstants.*;

public class SolarThingGraphQLDailyService {
	private final SimpleQueryHandler simpleQueryHandler;
	private final ZoneId zoneId;

	public SolarThingGraphQLDailyService(SimpleQueryHandler simpleQueryHandler, ZoneId zoneId) {
		this.simpleQueryHandler = simpleQueryHandler;
		this.zoneId = zoneId;
	}

	interface NodeAdder <T extends DailyData> {
		void addNodes(Collection<? super DataNode<Float>> nodesOut, List<TimestampedPacket<T>> timestampedPackets, List<AccumulationPair<T>> accumulationPairs, String sourceId, int fragmentId, long dayStartTimeMillis);
	}

	public class SolarThingFullDayStatusQuery {
		private final PacketGetter packetGetter;
		private final List<? extends FragmentedPacketGroup> sortedPackets;

		public SolarThingFullDayStatusQuery(PacketGetter packetGetter, List<? extends FragmentedPacketGroup> sortedPackets) {
			this.packetGetter = packetGetter;
			this.sortedPackets = sortedPackets;
		}

		private <T extends Identifiable & DailyData> List<DataNode<Float>> getPoints(Class<T> clazz, NodeAdder<T> nodeAdder) {
			Map<LocalDate, Map<IdentifierFragment, List<TimestampedPacket<T>>>> map = new HashMap<>();
			Map<IdentifierFragment, String> identifierFragmentSourceMap = new HashMap<>();
			for (PacketNode<T> packetNode : packetGetter.getPackets(clazz)) {
				long dateMillis = packetNode.getDateMillis();
				LocalDate date = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDate();

				T packet = packetNode.getPacket();
				IdentifierFragment identifierFragment = IdentifierFragment.create(packetNode.getFragmentId(), packet.getIdentifier());
				identifierFragmentSourceMap.put(identifierFragment, packetNode.getSourceId());
				Map<IdentifierFragment, List<TimestampedPacket<T>>> identifierMap = map.computeIfAbsent(date, (_date) -> new HashMap<>());
				identifierMap.computeIfAbsent(identifierFragment, (_identifier) -> new ArrayList<>()).add(new TimestampedPacket<>(packet, dateMillis));
			}
			Collection<DataNode<Float>> r = new TreeSet<>(DataNode::compareTo);
			for (Map.Entry<LocalDate, Map<IdentifierFragment, List<TimestampedPacket<T>>>> entry : map.entrySet()) {
				LocalDate date = entry.getKey();
				long dayStartTimeMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli();

				Map<IdentifierFragment, List<TimestampedPacket<T>>> identifierMap = entry.getValue();
				AccumulationConfig accumulationConfig = AccumulationConfig.createDefault(dayStartTimeMillis);
				for (Map.Entry<IdentifierFragment, List<TimestampedPacket<T>>> identifierFragmentListEntry : identifierMap.entrySet()) {
					IdentifierFragment identifierFragment = identifierFragmentListEntry.getKey();
					List<TimestampedPacket<T>> timestampedPackets = identifierFragmentListEntry.getValue();
					List<AccumulationPair<T>> accumulationPairs = AccumulationUtil.getAccumulationPairs(timestampedPackets, accumulationConfig);
					String sourceId = identifierFragmentSourceMap.get(identifierFragment);
					if (sourceId == null) {
						throw new AssertionError("No source ID for identifier fragment: " + identifierFragment);
					}

					nodeAdder.addNodes(r, timestampedPackets, accumulationPairs, sourceId, identifierFragment.getFragmentId(), dayStartTimeMillis);
				}
			}
			return new ArrayList<>(r);
		}
		private <T extends DailyData> void addAllPoints(Collection<? super DataNode<Float>> nodesOut, List<TimestampedPacket<T>> timestampedPackets, List<AccumulationPair<T>> accumulationPairs, String sourceId, int fragmentId, Function<T, Float> totalGetter) {
			T firstPacket = timestampedPackets.get(0).getPacket();
			List<AccumulationCalc.SumNode> sumNodes = AccumulationCalc.getTotals(accumulationPairs, totalGetter::apply, timestampedPackets);
			for (AccumulationCalc.SumNode sumNode : sumNodes) {
				nodesOut.add(new DataNode<>(sumNode.getSum(), firstPacket, sumNode.getDateMillis(), sourceId, fragmentId));
			}
		}
		private <T extends DailyData> void addDayPoints(Collection<? super DataNode<Float>> nodesOut, List<TimestampedPacket<T>> timestampedPackets, List<AccumulationPair<T>> accumulationPairs, String sourceId, int fragmentId, long dayStartTimeMillis, Function<T, Float> totalGetter) {
			T firstPacket = timestampedPackets.get(0).getPacket();
			float total = AccumulationCalc.getTotal(accumulationPairs, totalGetter::apply);
			nodesOut.add(new DataNode<>(total, firstPacket, dayStartTimeMillis, sourceId, fragmentId));
		}

		@GraphQLQuery(description = "Gives a list of a list entries. Each entry can be grouped by their identifier as entries may represent different devices")
		public @NotNull List<@NotNull DataNode<Float>> dailyKWH() {
			return getPoints(
					DailyChargeController.class,
					(nodesOut, timestampedPackets, dailyPairs, sourceId, fragmentId, dayStartTimeMillis) ->
							addAllPoints(nodesOut, timestampedPackets, dailyPairs, sourceId, fragmentId, DailyChargeController::getDailyKWH)
			);
		}
		@GraphQLQuery(description = "Gives a list of entries where each entry is a sum of the daily kWh at that instant in time for the day at that time.")
		public @NotNull List<@NotNull SimpleNode<Float>> dailyKWHSum() {
			Map<LocalDate, Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>>> map = new HashMap<>();
			for (FragmentedPacketGroup fragmentedPacketGroup : sortedPackets) {
				long dateMillis = fragmentedPacketGroup.getDateMillis(); // we have a common dateMillis for each fragmented packet group
				LocalDate date = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDate();
				for (Packet packet : fragmentedPacketGroup.getPackets()) {
					if (packet instanceof DailyChargeController) {
						int fragmentId = fragmentedPacketGroup.getFragmentId(packet);
						DailyChargeController dailyChargeController = (DailyChargeController) packet;
						IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, dailyChargeController.getIdentifier());
						Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>> identifierMap = map.computeIfAbsent(date, (_date) -> new HashMap<>());
						identifierMap.computeIfAbsent(identifierFragment, (_identifier) -> new ArrayList<>()).add(new TimestampedPacket<>(dailyChargeController, dateMillis));
					}
				}
			}
			Collection<SimpleNode<Float>> r = new TreeSet<>(Comparator.comparingLong(SimpleNode::getDateMillis));
			for (Map.Entry<LocalDate, Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>>> entry : map.entrySet()) {
				LocalDate date = entry.getKey();
				Map<Long, Float> sumMap = new HashMap<>();
				long dayStartTimeMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli();
				Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>> identifierMap = entry.getValue();
				AccumulationConfig accumulationConfig = AccumulationConfig.createDefault(dayStartTimeMillis);
				for (Map.Entry<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>> identifierFragmentListEntry : identifierMap.entrySet()) {
					List<TimestampedPacket<DailyChargeController>> timestampedPackets = identifierFragmentListEntry.getValue();
					List<AccumulationPair<DailyChargeController>> accumulationPairs = AccumulationUtil.getAccumulationPairs(timestampedPackets, accumulationConfig);
					List<AccumulationCalc.SumNode> sumNodes = AccumulationCalc.getTotals(accumulationPairs, DailyChargeController::getDailyKWH, timestampedPackets);
					for (AccumulationCalc.SumNode sumNode : sumNodes) {
						sumMap.compute(sumNode.getDateMillis(), (_date, oldValue) -> oldValue == null ? sumNode.getSum() : oldValue + sumNode.getSum());
					}
				}
				for (Map.Entry<Long, Float> sumEntry : sumMap.entrySet()) {
					r.add(new SimpleNode<>(sumEntry.getValue(), sumEntry.getKey()));
				}
			}
			return new ArrayList<>(r);
		}

		/**
		 *
		 * @return A list of {@link SimpleNode}s where each node is a different day
		 */
		@GraphQLQuery(description = "Gives entries where each entry is timestamped at the start of a certain day and its value represents the daily kWh of that device for that day. (Results can be grouped by their identifiers as there may be different devices)")
		public @NotNull List<@NotNull DataNode<Float>> singleDailyKWH() {
			return getPoints(
					DailyChargeController.class,
					(nodesOut, timestampedPackets, dailyPairs, sourceId, fragmentId, dayStartTimeMillis) ->
							addDayPoints(nodesOut, timestampedPackets, dailyPairs, sourceId, fragmentId, dayStartTimeMillis, DailyChargeController::getDailyKWH)
			);
		}

//		@GraphQLQuery
//		public @NotNull List<@NotNull SimpleNode<Float>> singleDailyKWHSum() {
//			return null;
//		}
	}


	@GraphQLQuery
	public SolarThingFullDayStatusQuery queryFullDay(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId){

		LocalDate fromDate = Instant.ofEpochMilli(from).atZone(zoneId).toLocalDate();
		LocalDate toDate = Instant.ofEpochMilli(to).atZone(zoneId).toLocalDate();
		List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(
				fromDate.atStartOfDay(zoneId).toInstant().toEpochMilli(),
				toDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1,
				sourceId
		);
		return new SolarThingFullDayStatusQuery(new BasicPacketGetter(packets, PacketFilter.KEEP_ALL), simpleQueryHandler.sortPackets(packets, sourceId));
	}
}
