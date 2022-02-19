package me.retrodaredevil.solarthing.rest.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.SourceIdentifierFragment;
import me.retrodaredevil.solarthing.rest.cache.CacheController;
import me.retrodaredevil.solarthing.rest.graphql.PacketFinder;
import me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.DataNode;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.data.BatteryRecordDataCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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


	@GraphQLQuery
	public @NotNull SolarThingBatteryRecordQuery queryBatteryRecord(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId
	) {
		List<IdentificationCacheDataPacket<BatteryRecordDataCache>> list = cacheController.getBatteryRecord(sourceId, from, to);
		return new SolarThingBatteryRecordQuery(list);
	}

}
