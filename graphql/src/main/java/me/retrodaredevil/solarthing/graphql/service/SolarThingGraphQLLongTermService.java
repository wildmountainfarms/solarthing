package me.retrodaredevil.solarthing.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.solar.common.AccumulatedChargeController;
import me.retrodaredevil.solarthing.solar.daily.DailyCalc;
import me.retrodaredevil.solarthing.solar.daily.DailyConfig;
import me.retrodaredevil.solarthing.solar.daily.DailyPair;
import me.retrodaredevil.solarthing.solar.daily.DailyUtil;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.retrodaredevil.solarthing.graphql.service.SchemaConstants.*;

public class SolarThingGraphQLLongTermService {

	private final SimpleQueryHandler simpleQueryHandler;
	private final ZoneId zoneId;

	public SolarThingGraphQLLongTermService(SimpleQueryHandler simpleQueryHandler, ZoneId zoneId) {
		this.simpleQueryHandler = simpleQueryHandler;
		this.zoneId = zoneId;
	}

	@GraphQLQuery
	public SolarThingLongTermQuery queryLongTermMillis(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId){

		List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(from, to, sourceId);
		DailyConfig dailyConfig = DailyConfig.createDefault(from);

		// Note that unlike most other places, here we do NOT call simpleQueryHandler.sortPackets.
		//   This is because sortPackets sometimes will disregard packets with a lower priority fragment ID.
		//   And, since we don't actually need the merged packets, there's no point in merging them.
		//   Also (2021.04.17), now that I think about it, we should probably have a better way to merge packets without sometimes losing ones with lower priority fragments. (TODO)
		return new SolarThingLongTermQuery(packets, dailyConfig);
	}
	@GraphQLQuery
	public SolarThingLongTermQuery queryLongTermMonth(
			@GraphQLArgument(name = "year") int year,
			@GraphQLArgument(name = "month") Month month,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId){
		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = yearMonth.atEndOfMonth();
		return queryLongTermMillis(
				start.atStartOfDay(zoneId).toInstant().toEpochMilli(),
				end.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1,
				sourceId
		);
	}
	public static class SolarThingLongTermQuery {
		private final List<? extends FragmentedPacketGroup> packetGroups;
		private final DailyConfig dailyConfig;

		public SolarThingLongTermQuery(List<? extends FragmentedPacketGroup> packetGroups, DailyConfig dailyConfig) {
			this.packetGroups = packetGroups;
			this.dailyConfig = dailyConfig;
		}

		@GraphQLQuery
		public Map<Integer, Map<Identifier, Float>> solarKWHIndividual() {
			Map<Integer, Map<Identifier, Float>> r = new HashMap<>();
			Map<IdentifierFragment, List<DailyPair<AccumulatedChargeController>>> chargeControllerMap = DailyUtil.getDailyPairs(DailyUtil.mapPackets(AccumulatedChargeController.class, packetGroups), dailyConfig);

			for (Map.Entry<IdentifierFragment, List<DailyPair<AccumulatedChargeController>>> entry : chargeControllerMap.entrySet()) {
				Map<Identifier, Float> identifierMap = r.computeIfAbsent(entry.getKey().getFragmentId(), (_key) -> new HashMap<>());
				identifierMap.put(entry.getKey().getIdentifier(), DailyCalc.getTotal(entry.getValue(), AccumulatedChargeController::getDailyKWH));
			}

			return r;
		}
		@GraphQLQuery
		public Map<Integer, Map<Identifier, Float>> solarKWHIndividualPercent() {
			Map<Integer, Map<Identifier, Float>> r = new HashMap<>();
			Map<Integer, Map<Identifier, Float>> individualKWH = solarKWHIndividual();
			float total = solarKWHTotal(individualKWH);
			// individualKWH is a mutable map, so we can just change its values really easily to turn it into an individualKWHPercent map
			for (Map<Identifier, Float> map : solarKWHIndividual().values()) {
				for (Map.Entry<Identifier, Float> entry : map.entrySet()) {
					entry.setValue(entry.getValue() / total);
				}
			}
			return individualKWH;
		}
		private float solarKWHTotal(Map<Integer, Map<Identifier, Float>> individualKWH) {
			float r = 0.0f;
			for (Map<Identifier, Float> map : individualKWH.values()) {
				for (float value : map.values()) {
					r += value;
				}
			}
			return r;
		}
		@GraphQLQuery
		public float solarKWHTotal() {
			return solarKWHTotal(solarKWHIndividual());
		}

		public float acUseTimeHours() {
			throw new UnsupportedOperationException("TODO");
		}
		public float fxInverterKWH() {
			throw new UnsupportedOperationException("TODO");
		}
		public float fxChargerKWH() {
			throw new UnsupportedOperationException("TODO");
		}
		public float fxBuyKWH() {
			throw new UnsupportedOperationException("TODO");
		}
		public float fxSellKWH() {
			throw new UnsupportedOperationException("TODO");
		}
	}
}
