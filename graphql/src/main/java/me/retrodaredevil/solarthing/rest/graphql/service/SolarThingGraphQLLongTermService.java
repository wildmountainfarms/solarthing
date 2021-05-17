package me.retrodaredevil.solarthing.rest.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.DataPoint;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.solar.common.AccumulatedChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationCalc;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationConfig;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationPair;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationUtil;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;

import static me.retrodaredevil.solarthing.rest.graphql.service.SchemaConstants.*;

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
		AccumulationConfig accumulationConfig = AccumulationConfig.createDefault(from);

		// Note that unlike most other places, here we do NOT call simpleQueryHandler.sortPackets.
		//   This is because sortPackets sometimes will disregard packets with a lower priority fragment ID.
		//   And, since we don't actually need the merged packets, there's no point in merging them.
		//   Also (2021.04.17), now that I think about it, we should probably have a better way to merge packets without sometimes losing ones with lower priority fragments. (TODO)
		return new SolarThingLongTermQuery(packets, accumulationConfig, sourceId);
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
		private final AccumulationConfig accumulationConfig;
		private final String sourceId;

		public SolarThingLongTermQuery(List<? extends FragmentedPacketGroup> packetGroups, AccumulationConfig accumulationConfig, String sourceId) {
			this.packetGroups = packetGroups;
			this.accumulationConfig = accumulationConfig;
			this.sourceId = sourceId;
		}

		@GraphQLQuery
		public @NotNull List<@NotNull DataPoint<Float>> solarKWHIndividual() {
			List<DataPoint<Float>> r = new ArrayList<>();
			Map<IdentifierFragment, List<AccumulationPair<DailyChargeController>>> chargeControllerMap = AccumulationUtil.getAccumulationPairs(AccumulationUtil.mapPackets(DailyChargeController.class, packetGroups), accumulationConfig);

			for (Map.Entry<IdentifierFragment, List<AccumulationPair<DailyChargeController>>> entry : chargeControllerMap.entrySet()) {
				float total = AccumulationCalc.getTotal(entry.getValue(), AccumulatedChargeController::getDailyKWH);
				r.add(new DataPoint<>(total, entry.getValue().get(0).getStartPacket().getPacket(), sourceId, entry.getKey().getFragmentId()));
			}

			return r;
		}
		@GraphQLQuery
		public @NotNull List<@NotNull DataPoint<Float>> solarKWHIndividualPercent() {
			List<DataPoint<Float>> individualKWH = solarKWHIndividual();
			float total = solarKWHTotal(individualKWH);
			// individualKWH is a mutable list, so we can just change its values really easily to turn it into an individualKWHPercent list
			for (ListIterator<DataPoint<Float>> iterator = individualKWH.listIterator(); iterator.hasNext(); ) {
				DataPoint<Float> dataPoint = iterator.next();
				iterator.set(new DataPoint<>(dataPoint.getData() / total, dataPoint.getIdentifiable(), dataPoint.getSourceId(), dataPoint.getFragmentId()));
			}
			return individualKWH;
		}
		private float solarKWHTotal(@NotNull List<@NotNull DataPoint<Float>> individualKWH) {
			float r = 0.0f;
			for (DataPoint<Float> dataPoint : individualKWH) {
				r += dataPoint.getData();
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
