package me.retrodaredevil.solarthing.rest.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.packets.identification.SourceIdentifierFragment;
import me.retrodaredevil.solarthing.rest.cache.CacheController;
import me.retrodaredevil.solarthing.rest.graphql.PacketFinder;
import me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.DataNode;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.type.cache.packets.data.BatteryRecordDataCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static me.retrodaredevil.solarthing.rest.graphql.service.SchemaConstants.*;

public class SolarThingGraphQLBatteryRecordService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolarThingGraphQLBatteryRecordService.class);

	private final SimpleQueryHandler simpleQueryHandler;
	private final CacheController cacheController;

	public SolarThingGraphQLBatteryRecordService(SimpleQueryHandler simpleQueryHandler, CacheController cacheController) {
		this.simpleQueryHandler = simpleQueryHandler;
		this.cacheController = cacheController;
	}

	public class SolarThingBatteryRecordQuery {
		private final List<IdentificationCacheDataPacket<BatteryRecordDataCache>> data;
		private final PacketFinder packetFinder;

		public SolarThingBatteryRecordQuery(List<IdentificationCacheDataPacket<BatteryRecordDataCache>> data) {
			this.data = data;
			packetFinder = new PacketFinder(simpleQueryHandler); // TODO consider using a shared PacketFinder so there is a shared cache
		}

		@GraphQLQuery
		public @NotNull List<@NotNull DataNode<Double>> averageBatteryVoltage() {
			List<DataNode<Double>> r = new ArrayList<>();
			for (var cache : data) {
				long midpointMillis = cache.getPeriodStartDateMillis() + cache.getPeriodDurationMillis() / 2;
				String sourceId = cache.getSourceId();
				for (var node : cache.getNodes()) {
					BatteryRecordDataCache batteryRecord = node.getData();
					BatteryRecordDataCache.Record record = batteryRecord.getRecord();
					if (record != null) {
						double voltHours = record.getBatteryVoltageHours();
						double hours = record.getKnownDurationMillis() / (1000.0 * 60 * 60);
						double average = voltHours / hours; // integral over interval BAAYYBEEEEE

						SourceIdentifierFragment sourceIdentifierFragment = SourceIdentifierFragment.create(sourceId, node.getFragmentId(), batteryRecord.getIdentifier());
						Identifiable identifiable = packetFinder.findPacket(sourceIdentifierFragment.getIdentifierFragment(), cache.getPeriodStartDateMillis(), cache.getPeriodEndDateMillis());

						if (identifiable != null) {
							r.add(new DataNode<>(
									average,
									identifiable,
									midpointMillis,
									sourceId,
									node.getFragmentId()
							));
						} else {
							LOGGER.warn("Could not find identifiable for " + sourceIdentifierFragment);
						}
					}
				}
			}
			return r;
		}
	}

	public class SolarThingBatteryEstimate {
		private final List<IdentificationCacheDataPacket<BatteryRecordDataCache>> data;
		private final PacketFinder packetFinder;

		public SolarThingBatteryEstimate(List<IdentificationCacheDataPacket<BatteryRecordDataCache>> data) {
			this.data = data;
			packetFinder = new PacketFinder(simpleQueryHandler);
		}

		@GraphQLQuery
		public @NotNull List<DataNode<Double>> queryEstimate(@GraphQLArgument(name = "ratio") double ratio) {
			var first = data.get(0);
			var last = data.get(data.size() - 1);

			int lastFirstIndex = data.size();
			long lastStartDateMillis = last.getPeriodEndDateMillis();

			Map<IdentifierFragment, Double> averageMap = new HashMap<>();
			Map<IdentifierFragment, Double> remainingWeightMap = new HashMap<>();
			while (true) {
				long periodDurationMillis = lastStartDateMillis - first.getPeriodEndDateMillis();
				long minimumEndDateMillis = lastStartDateMillis - (long) Math.ceil(periodDurationMillis * ratio);


				Integer firstIndex = null;

				IdentificationCacheDataPacket<BatteryRecordDataCache> combined = null;

				for (int i = 0; i < lastFirstIndex; i++) {
					IdentificationCacheDataPacket<BatteryRecordDataCache> packet = data.get(i);
					if (packet.getPeriodEndDateMillis() < minimumEndDateMillis && i != lastFirstIndex - 1) {
						continue;
					}
					//
					if (firstIndex == null) {
						firstIndex = i;
					}

					if (combined == null) {
						combined = packet;
					} else {
						combined = combined.combine(packet);
					}
					for (IdentificationCacheNode<BatteryRecordDataCache> cache : packet.getNodes()) {
						packetFinder.findPacket(IdentifierFragment.create(cache.getFragmentId(), cache.getData().getIdentifier()), packet.getPeriodStartDateMillis(), packet.getPeriodEndDateMillis());
					}
				}
				if (combined == null) {
					break;
				}
				//noinspection ConstantConditions
				assert firstIndex != null;

				lastFirstIndex = firstIndex;
				lastStartDateMillis = data.get(firstIndex).getPeriodStartDateMillis();

				for (IdentificationCacheNode<BatteryRecordDataCache> cache : combined.getNodes()) {
					IdentifierFragment identifierFragment = IdentifierFragment.create(cache.getFragmentId(), cache.getData().getIdentifier());
					var data = cache.getData();
					var record = data.getRecord();
					if (record == null) {
						continue;
					}
					double remainingWeight = remainingWeightMap.getOrDefault(identifierFragment, 1.0);
					double currentWeight = (1 - ratio) * remainingWeight;
					remainingWeightMap.put(identifierFragment, remainingWeight - currentWeight);

					double hours = (record.getKnownDurationMillis() + record.getGapDurationMillis()) / (1000.0 * 60 * 60);
					double average = (record.getBatteryVoltageHours() + record.getGapBatteryVoltageHours()) / hours;

					double weightedAveragePiece = average * currentWeight;
					averageMap.compute(identifierFragment, (_identifier, currentAverage) -> currentAverage == null ? weightedAveragePiece : currentAverage + average);
				}

			}
			return averageMap.entrySet().stream().map(entry -> {
				var identifierFragment = entry.getKey();
				double rawAverage = entry.getValue();
				double remainingWeight = remainingWeightMap.get(identifierFragment);
				double average = rawAverage / (1 - remainingWeight);

				Identifiable identifiable = packetFinder.getCached(identifierFragment);
				if (identifiable == null) {
					LOGGER.warn("Could not get a cached Identifiable for identifier: " + identifierFragment.getIdentifier());
					return null;
				}

				return new DataNode<>(average, identifiable, last.getPeriodEndDateMillis(), last.getSourceId(), identifierFragment.getFragmentId());
			})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		}
	}


	@GraphQLQuery
	public @NotNull SolarThingBatteryRecordQuery queryBatteryRecord(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId
	) {
		List<IdentificationCacheDataPacket<BatteryRecordDataCache>> list = cacheController.getBatteryRecord(sourceId, from, to);
		return new SolarThingBatteryRecordQuery(list);
	}

	@GraphQLQuery
	public @NotNull SolarThingBatteryEstimate queryBatteryEstimate(
			@GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId,
			@GraphQLArgument(name = "duration", description = DESCRIPTION_TO) Duration duration
			) {
		long from = to - duration.toMillis();
		List<IdentificationCacheDataPacket<BatteryRecordDataCache>> list = cacheController.getBatteryRecord(sourceId, from, to);
		return new SolarThingBatteryEstimate(list);
	}

}
