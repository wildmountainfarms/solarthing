package me.retrodaredevil.solarthing.rest.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.type.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.packets.identification.SourceIdentifierFragment;
import me.retrodaredevil.solarthing.rest.cache.CacheController;
import me.retrodaredevil.solarthing.rest.graphql.PacketFinder;
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
import me.retrodaredevil.solarthing.solar.accumulation.value.FloatAccumulationValue;
import me.retrodaredevil.solarthing.solar.accumulation.value.FloatAccumulationValueFactory;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationCalc;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationConfig;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationPair;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static me.retrodaredevil.solarthing.rest.graphql.service.SchemaConstants.*;

public class SolarThingGraphQLDailyService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolarThingGraphQLDailyService.class);

	private final SimpleQueryHandler simpleQueryHandler;
	private final ZoneId zoneId;
	private final CacheController cacheController;

	public SolarThingGraphQLDailyService(SimpleQueryHandler simpleQueryHandler, ZoneId zoneId, CacheController cacheController) {
		this.simpleQueryHandler = simpleQueryHandler;
		this.zoneId = zoneId;
		this.cacheController = cacheController;
	}

	interface NodeAdder <T extends DailyData> {
		void addNodes(Collection<? super DataNode<Float>> nodesOut, List<TimestampedPacket<T>> timestampedPackets, List<AccumulationPair<T>> accumulationPairs, String sourceId, int fragmentId, long dayStartTimeMillis);
	}

	public interface SolarThingFullDayStatusQuery {
		@GraphQLQuery(description = "Gives a list of a list entries. Each entry can be grouped by their identifier as entries may represent different devices")
		@NotNull List<@NotNull DataNode<Float>> dailyKWH();

		@GraphQLQuery(description = "Gives a list of entries where each entry is a sum of the daily kWh at that instant in time for the day at that time.")
		@NotNull List<@NotNull SimpleNode<Float>> dailyKWHSum();

		/**
		 * @return A list of {@link SimpleNode}s where each node is a different day
		 */
		@GraphQLQuery(description = "Gives entries where each entry is timestamped at the start of a certain day and its value represents the daily kWh of that device for that day. (Results can be grouped by their identifiers as there may be different devices)")
		@NotNull List<@NotNull DataNode<Float>> singleDailyKWH();
	}

	public class SimpleSolarThingFullDayStatusQuery implements SolarThingFullDayStatusQuery {
		private final PacketGetter packetGetter;
		private final List<? extends FragmentedPacketGroup> sortedPackets;

		public SimpleSolarThingFullDayStatusQuery(PacketGetter packetGetter, List<? extends FragmentedPacketGroup> sortedPackets) {
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
			List<AccumulationCalc.SumNode<FloatAccumulationValue>> sumNodes = AccumulationCalc.getTotals(accumulationPairs, FloatAccumulationValue.convert(totalGetter), timestampedPackets, FloatAccumulationValueFactory.getInstance());
			for (AccumulationCalc.SumNode<FloatAccumulationValue> sumNode : sumNodes) {
				nodesOut.add(new DataNode<>(sumNode.getSum().getValue(), firstPacket, sumNode.getDateMillis(), sourceId, fragmentId));
			}
		}
		private <T extends DailyData> void addDayPoints(Collection<? super DataNode<Float>> nodesOut, List<TimestampedPacket<T>> timestampedPackets, List<AccumulationPair<T>> accumulationPairs, String sourceId, int fragmentId, long dayStartTimeMillis, Function<T, Float> totalGetter) {
			T firstPacket = timestampedPackets.get(0).getPacket();
			float total = AccumulationCalc.getTotal(accumulationPairs, FloatAccumulationValue.convert(totalGetter), FloatAccumulationValueFactory.getInstance()).getValue();
			nodesOut.add(new DataNode<>(total, firstPacket, dayStartTimeMillis, sourceId, fragmentId));
		}

		@Override
		public @NotNull List<@NotNull DataNode<Float>> dailyKWH() {
			return getPoints(
					DailyChargeController.class,
					(nodesOut, timestampedPackets, dailyPairs, sourceId, fragmentId, dayStartTimeMillis) ->
							addAllPoints(nodesOut, timestampedPackets, dailyPairs, sourceId, fragmentId, DailyChargeController::getDailyKWH)
			);
		}
		@Override
		public @NotNull List<@NotNull SimpleNode<Float>> dailyKWHSum() {
			Map<LocalDate, Map<IdentifierFragment, List<TimestampedPacket<DailyChargeController>>>> map = new HashMap<>();
			for (FragmentedPacketGroup fragmentedPacketGroup : sortedPackets) {
				long dateMillis = fragmentedPacketGroup.getDateMillis(); // we have a common dateMillis for each fragmented packet group
				LocalDate date = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDate();
				for (Packet packet : fragmentedPacketGroup.getPackets()) {
					if (packet instanceof DailyChargeController dailyChargeController) {
						int fragmentId = fragmentedPacketGroup.getFragmentId(packet);
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
					List<AccumulationCalc.SumNode<FloatAccumulationValue>> sumNodes = AccumulationCalc.getTotals(accumulationPairs, FloatAccumulationValue.convert(DailyChargeController::getDailyKWH), timestampedPackets, FloatAccumulationValueFactory.getInstance());
					for (AccumulationCalc.SumNode<FloatAccumulationValue> sumNode : sumNodes) {
						sumMap.compute(sumNode.getDateMillis(), (_date, oldValue) -> oldValue == null ? sumNode.getSum().getValue() : oldValue + sumNode.getSum().getValue());
					}
				}
				for (Map.Entry<Long, Float> sumEntry : sumMap.entrySet()) {
					r.add(new SimpleNode<>(sumEntry.getValue(), sumEntry.getKey()));
				}
			}
			return new ArrayList<>(r);
		}

		@Override
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

	public class CacheSolarThingFullDayStatusQuery implements SolarThingFullDayStatusQuery {

		private final List<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> chargeControllerData;
		private final PacketFinder packetFinder;

		public CacheSolarThingFullDayStatusQuery(List<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> chargeControllerData) {
			this.chargeControllerData = chargeControllerData;
			packetFinder = new PacketFinder(simpleQueryHandler);
		}

		@Override
		public @NotNull List<@NotNull DataNode<Float>> dailyKWH() {
			List<DataNode<Float>> r = new ArrayList<>();
			Map<LocalDate, Map<SourceIdentifierFragment, ChargeControllerAccumulationDataCache>> dateToControllerCache = new HashMap<>();
			for (IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache> cache : chargeControllerData) {
				LocalDate date = cache.getPeriodStart().atZone(zoneId).toLocalDate();
				long midpointMillis = cache.getPeriodStartDateMillis() + cache.getPeriodDurationMillis() / 2;
				String sourceId = cache.getSourceId();
				for (IdentificationCacheNode<ChargeControllerAccumulationDataCache> node : cache.getNodes()) {
					ChargeControllerAccumulationDataCache data = node.getData();
					SourceIdentifierFragment sourceIdentifierFragment = SourceIdentifierFragment.create(sourceId, node.getFragmentId(), data.getIdentifier());
					ChargeControllerAccumulationDataCache total = dateToControllerCache.getOrDefault(date, Collections.emptyMap()).get(sourceIdentifierFragment);
					if (total == null) {
						total = data;
					} else {
						total = total.combine(data);
					}
					Identifiable identifiable = packetFinder.findPacket(sourceIdentifierFragment.getIdentifierFragment(), cache.getPeriodStartDateMillis(), cache.getPeriodEndDateMillis());
					if (identifiable != null) {
						r.add(new DataNode<>(data.getGenerationKWH(), identifiable, midpointMillis, sourceId, sourceIdentifierFragment.getIdentifierFragment().getFragmentId()));
					} else {
						LOGGER.warn("Could not find identifiable for " + sourceIdentifierFragment);
					}
					dateToControllerCache.computeIfAbsent(date, _date -> new HashMap<>()).put(sourceIdentifierFragment, total);
				}
			}
			return r;
		}

		@Override
		public @NotNull List<@NotNull SimpleNode<Float>> dailyKWHSum() {
			throw new UnsupportedOperationException();
		}

		@Override
		public @NotNull List<@NotNull DataNode<Float>> singleDailyKWH() {
			Map<LocalDate, Map<SourceIdentifierFragment, ChargeControllerAccumulationDataCache>> dateToControllerCache = new HashMap<>();
			Map<SourceIdentifierFragment, Identifiable> identifierFragmentToIdentifiableMap = new HashMap<>();
			for (IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache> cache : chargeControllerData) {
				LocalDate date = cache.getPeriodStart().atZone(zoneId).toLocalDate();
				String sourceId = cache.getSourceId();
				for (IdentificationCacheNode<ChargeControllerAccumulationDataCache> node : cache.getNodes()) {
					ChargeControllerAccumulationDataCache data = node.getData();
					SourceIdentifierFragment sourceIdentifierFragment = SourceIdentifierFragment.create(sourceId, node.getFragmentId(), data.getIdentifier());
					ChargeControllerAccumulationDataCache total = dateToControllerCache.getOrDefault(date, Collections.emptyMap()).get(sourceIdentifierFragment);
					if (total == null) {
						total = data;
					} else {
						total = total.combine(data);
					}
					// We don't want to search for the identifier of data this is unknown, because that means the packet isn't there!
					if (!data.isUnknown() && !identifierFragmentToIdentifiableMap.containsKey(sourceIdentifierFragment)) {
						Identifiable identifiable = packetFinder.findPacket(sourceIdentifierFragment.getIdentifierFragment(), cache.getPeriodStartDateMillis(), cache.getPeriodEndDateMillis());
						if (identifiable != null) {
							identifierFragmentToIdentifiableMap.put(sourceIdentifierFragment, identifiable);
						}
					}
					dateToControllerCache.computeIfAbsent(date, _date -> new HashMap<>()).put(sourceIdentifierFragment, total);
				}
			}
			List<DataNode<Float>> r = new ArrayList<>();
			for (Map.Entry<LocalDate, Map<SourceIdentifierFragment, ChargeControllerAccumulationDataCache>> entry : dateToControllerCache.entrySet()) {
				LocalDate date = entry.getKey();
				Map<SourceIdentifierFragment, ChargeControllerAccumulationDataCache> map = entry.getValue();
				long dayStart = date.atStartOfDay(zoneId).toInstant().toEpochMilli();
				for (Map.Entry<SourceIdentifierFragment, ChargeControllerAccumulationDataCache> entry2 : map.entrySet()) {
					SourceIdentifierFragment sourceIdentifierFragment = entry2.getKey();
					Identifiable identifiable = identifierFragmentToIdentifiableMap.get(sourceIdentifierFragment);
					requireNonNull(identifiable, "The identifiable for " + sourceIdentifierFragment + " is null!");
					float value = entry2.getValue().getGenerationKWH();
					r.add(new DataNode<>(value, identifiable, dayStart, sourceIdentifierFragment.getSourceId(), sourceIdentifierFragment.getIdentifierFragment().getFragmentId()));
				}
			}
			return r;
		}
	}

	@GraphQLQuery(description = "Queries each day present in the from..to time range. Optionally leave from as null to guarantee a query for a single day only.")
	public SolarThingFullDayStatusQuery queryFullDay(
			@GraphQLArgument(name = "from", description = "The epoch millis value that will be used to determine the starting day. Set to null to guarantee a query of a single day.") @Nullable Long from,
			@GraphQLArgument(name = "to", description = "The epoch millis value that will be used to determine the ending day.") long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId,
			@GraphQLArgument(name = "useCache", defaultValue = "false") boolean useCache){

		LocalDate fromDate = Instant.ofEpochMilli(from == null ? to : from).atZone(zoneId).toLocalDate();
		LocalDate toDate = Instant.ofEpochMilli(to).atZone(zoneId).toLocalDate();
		long queryStart = fromDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
		long queryEnd = toDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1;
		if (useCache) {
			return new CacheSolarThingFullDayStatusQuery(cacheController.getChargeControllerAccumulation(sourceId, queryStart, queryEnd));
		} else {
			List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(
					queryStart,
					queryEnd,
					sourceId
			);
			return new SimpleSolarThingFullDayStatusQuery(new BasicPacketGetter(packets, PacketFilter.KEEP_ALL), simpleQueryHandler.sortPackets(packets, sourceId));
		}
	}
}
