package me.retrodaredevil.solarthing.rest.graphql.service.statistics;

import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.rest.cache.CacheController;
import me.retrodaredevil.solarthing.rest.graphql.PacketFinder;
import me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.DataNode;
import me.retrodaredevil.solarthing.rest.graphql.service.SolarThingGraphQLBatteryRecordService;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.data.FXAccumulationDataCache;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * This class is designed to contain queries relating to statistics over a particular time range.
 * <p>
 * Example queries that this should support:
 * <ul>
 *     <li>energy used between 12am and 3am (kWh)</li>
 *     <li>average battery voltage</li>
 * </ul>
 * <p>
 * Hopefully this can one day encompass the logic that {@link SolarThingGraphQLBatteryRecordService} has so we can generalize it better
 */
public class SolarThingGraphQLStatisticsService {

	private final SimpleQueryHandler simpleQueryHandler;
	private final CacheController cacheController;

	public SolarThingGraphQLStatisticsService(SimpleQueryHandler simpleQueryHandler, CacheController cacheController) {
		this.simpleQueryHandler = simpleQueryHandler;
		this.cacheController = cacheController;
	}


	public class SolarThingStatisticsQuery {
		private final long from;
		private final long to;
		private final @NotNull String sourceId;

		public SolarThingStatisticsQuery(long from, long to, @NotNull String sourceId) {
			this.from = from;
			this.to = to;
			this.sourceId = requireNonNull(sourceId);
		}

		@GraphQLQuery
		public @NotNull SolarThingFXStatisticsQuery fxStatistics() {
			List<IdentificationCacheDataPacket<FXAccumulationDataCache>> list = cacheController.getFXAccumulation(sourceId, from, to);
			return new SolarThingFXStatisticsQuery(list);
		}
	}

	public class StatisticsResult {
		// TODO
		//   Previously, SolarThing Server GraphQL has always taken the responsibility of exploding data points to easily be consumed by Wild GraphQL Data Source.
		//   We no longer need to do that and should stop using DataNode

		@GraphQLQuery
		public @NotNull List<@NotNull DataNode<Double>> average() {
			throw new UnsupportedOperationException("TODO");
		}
	}

	public class SolarThingFXStatisticsQuery {
		private final List<IdentificationCacheDataPacket<FXAccumulationDataCache>> data;
		private final PacketFinder packetFinder;

		public SolarThingFXStatisticsQuery(List<IdentificationCacheDataPacket<FXAccumulationDataCache>> data) {
			this.data = data;
			this.packetFinder = new PacketFinder(simpleQueryHandler);
		}
	}
}
