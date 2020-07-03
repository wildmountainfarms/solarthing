package me.retrodaredevil.solarthing.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.graphql.packets.DataNode;
import me.retrodaredevil.solarthing.graphql.packets.PacketFilter;
import me.retrodaredevil.solarthing.graphql.packets.PacketNode;
import me.retrodaredevil.solarthing.graphql.packets.SimpleNode;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.daily.DailyCalc;
import me.retrodaredevil.solarthing.solar.daily.DailyConfig;
import me.retrodaredevil.solarthing.solar.daily.DailyPair;
import me.retrodaredevil.solarthing.solar.daily.DailyUtil;

import java.util.*;

public class SolarThingGraphQLDailyService {
	private final SimpleQueryHandler simpleQueryHandler;
	private final TimeZone timeZone;

	public SolarThingGraphQLDailyService(SimpleQueryHandler simpleQueryHandler, TimeZone timeZone) {
		this.simpleQueryHandler = simpleQueryHandler;
		this.timeZone = timeZone;
	}

	public class SolarThingFullDayStatusQuery {
		private final PacketGetter packetGetter;
		private final List<? extends FragmentedPacketGroup> sortedPackets;

		public SolarThingFullDayStatusQuery(PacketGetter packetGetter, List<? extends FragmentedPacketGroup> sortedPackets) {
			this.packetGetter = packetGetter;
			this.sortedPackets = sortedPackets;
		}
		@GraphQLQuery
		public @NotNull List<@NotNull DataNode<Float>> dailyKWH() {
			Map<SimpleDate, Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>>> map = new HashMap<>();
			Map<IdentifierFragment, String> identifierFragmentSourceMap = new HashMap<>();
			for (PacketNode<DailyChargeController> packetNode : packetGetter.getPackets(DailyChargeController.class)) {
				long dateMillis = packetNode.getDateMillis();
				SimpleDate date = SimpleDate.fromDateMillis(dateMillis, timeZone);
				DailyChargeController dailyChargeController = packetNode.getPacket();
				IdentifierFragment identifierFragment = IdentifierFragment.create(packetNode.getFragmentId(), dailyChargeController.getIdentifier());
				identifierFragmentSourceMap.put(identifierFragment, packetNode.getSourceId());
				Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>> identifierMap = map.computeIfAbsent(date, (_date) -> new HashMap<>());
				identifierMap.computeIfAbsent(identifierFragment, (_identifier) -> new ArrayList<>()).add(new TimestampedPacket<>(dailyChargeController, dateMillis));
			}
			List<DataNode<Float>> r = new ArrayList<>();
			for (Map.Entry<SimpleDate, Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>>> entry : map.entrySet()) { // TODO iterate over past dates first
				SimpleDate date = entry.getKey();
				long dayStartTimeMillis = date.getDayStartDateMillis(timeZone);
				Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>> identifierMap = entry.getValue();
				DailyConfig dailyConfig = DailyConfig.createDefault(dayStartTimeMillis);
				for (Map.Entry<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>> identifierFragmentListEntry : identifierMap.entrySet()) {
					IdentifierFragment identifierFragment = identifierFragmentListEntry.getKey();
					List<TimestampedPacket<DailyChargeController>> timestampedPackets = identifierFragmentListEntry.getValue();
					DailyChargeController firstPacket = timestampedPackets.get(0).getPacket();
					List<DailyPair<DailyChargeController>> dailyPairs = DailyUtil.getDailyPairs(timestampedPackets, dailyConfig);
					if (dailyPairs.get(0).getStartPacket().getPacket() != timestampedPackets.get(0).getPacket()) {
						throw new AssertionError("Start packet is not expected!");
					}
					if (dailyPairs.get(dailyPairs.size() - 1).getLatestPacket().getPacket() != timestampedPackets.get(timestampedPackets.size() - 1).getPacket()) {
						throw new AssertionError("End packet is not expected!");
					}
					List<DailyCalc.SumNode> sumNodes = DailyCalc.getTotals(dailyPairs, DailyChargeController::getDailyKWH, timestampedPackets);
					String sourceId = identifierFragmentSourceMap.get(identifierFragment);
					if (sourceId == null) {
						throw new AssertionError("No source ID for identifier fragment: " + identifierFragment);
					}
					for (DailyCalc.SumNode sumNode : sumNodes) {
						r.add(new DataNode<>(sumNode.getSum(), firstPacket, sumNode.getDateMillis(), sourceId, identifierFragment.getFragmentId()));
					}
				}
			}
			return r;
		}
		@GraphQLQuery
		public @NotNull List<@NotNull SimpleNode<Float>> dailyKWHSum() {
			Map<SimpleDate, Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>>> map = new HashMap<>();
			for (FragmentedPacketGroup fragmentedPacketGroup : sortedPackets) {
				long dateMillis = fragmentedPacketGroup.getDateMillis(); // we have a common dateMillis for each fragmented packet group
				SimpleDate date = SimpleDate.fromDateMillis(dateMillis, timeZone);
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
			for (Map.Entry<SimpleDate, Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>>> entry : map.entrySet()) {
				SimpleDate date = entry.getKey();
				Map<Long, Float> sumMap = new HashMap<>();
				long dayStartTimeMillis = date.getDayStartDateMillis(timeZone);
				Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>> identifierMap = entry.getValue();
				DailyConfig dailyConfig = DailyConfig.createDefault(dayStartTimeMillis);
				for (Map.Entry<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>> identifierFragmentListEntry : identifierMap.entrySet()) {
					List<TimestampedPacket<DailyChargeController>> timestampedPackets = identifierFragmentListEntry.getValue();
					List<DailyPair<DailyChargeController>> dailyPairs = DailyUtil.getDailyPairs(timestampedPackets, dailyConfig);
					List<DailyCalc.SumNode> sumNodes = DailyCalc.getTotals(dailyPairs, DailyChargeController::getDailyKWH, timestampedPackets);
					for (DailyCalc.SumNode sumNode : sumNodes) {
						sumMap.compute(sumNode.getDateMillis(), (_date, oldValue) -> oldValue == null ? sumNode.getSum() : oldValue + sumNode.getSum());
					}
				}
				for (Map.Entry<Long, Float> sumEntry : sumMap.entrySet()) {
					r.add(new SimpleNode<>(sumEntry.getValue(), sumEntry.getKey()));
				}
			}
			return new ArrayList<>(r);
		}

//		@GraphQLQuery
//		public @NotNull List<@NotNull SimpleNode<Float>> singleDailyKWH();
//		@GraphQLQuery
//		public @NotNull List<@NotNull SimpleNode<Float>> singleDailyKWHSum();
	}


	@GraphQLQuery
	public SolarThingFullDayStatusQuery queryFullDay(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @Nullable String sourceId){

		SimpleDate fromDate = SimpleDate.fromDateMillis(from, timeZone);
		SimpleDate toDate = SimpleDate.fromDateMillis(to, timeZone);
		List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(fromDate.getDayStartDateMillis(timeZone), to, sourceId);
		return new SolarThingFullDayStatusQuery(new BasicPacketGetter(packets, PacketFilter.KEEP_ALL), simpleQueryHandler.sortPackets(packets, sourceId));
	}
}
