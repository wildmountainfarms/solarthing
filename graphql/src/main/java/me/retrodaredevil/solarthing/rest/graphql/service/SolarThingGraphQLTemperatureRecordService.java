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
import me.retrodaredevil.solarthing.type.cache.packets.data.TemperatureRecordDataCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static me.retrodaredevil.solarthing.rest.graphql.service.SchemaConstants.*;

public class SolarThingGraphQLTemperatureRecordService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolarThingGraphQLTemperatureRecordService.class);

	private final SimpleQueryHandler simpleQueryHandler;
	private final CacheController cacheController;
	private final ZoneId zoneId;

	public SolarThingGraphQLTemperatureRecordService(SimpleQueryHandler simpleQueryHandler, CacheController cacheController, ZoneId zoneId) {
		this.simpleQueryHandler = simpleQueryHandler;
		this.cacheController = cacheController;
		this.zoneId = zoneId;
	}

	public class SolarThingTemperatureRecordQuery {
		private final List<IdentificationCacheDataPacket<TemperatureRecordDataCache>> data;
		private final PacketFinder packetFinder;

		public SolarThingTemperatureRecordQuery(List<IdentificationCacheDataPacket<TemperatureRecordDataCache>> data) {
			this.data = data;
			packetFinder = new PacketFinder(simpleQueryHandler);
		}

		@GraphQLQuery
		public @NotNull List<@NotNull DataNode<Double>> averageTemperatureVoltage() {
			List<DataNode<Double>> r = new ArrayList<>();
			for (var cache : data) {
				long midpointMillis = cache.getPeriodStartDateMillis() + cache.getPeriodDurationMillis() / 2;
				String sourceId = cache.getSourceId();
				for (var node : cache.getNodes()) {
					TemperatureRecordDataCache temperatureRecord = node.getData();
					TemperatureRecordDataCache.Record record = temperatureRecord.getRecord();
					if (record != null) {
						double voltHours = record.getTemperatureCelsiusHours();
						double hours = record.getKnownDurationMillis() / (1000.0 * 60 * 60);
						double average = voltHours / hours; // integral over interval BAAYYBEEEEE

						SourceIdentifierFragment sourceIdentifierFragment = SourceIdentifierFragment.create(sourceId, node.getFragmentId(), temperatureRecord.getIdentifier());
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
	public @NotNull SolarThingTemperatureRecordQuery queryTemperatureRecordFullDay(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId
	) {
		LocalDate fromDate = Instant.ofEpochMilli(from).atZone(zoneId).toLocalDate();
		LocalDate toDate = Instant.ofEpochMilli(to).atZone(zoneId).toLocalDate();
		long queryStart = fromDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
		long queryEnd = toDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1;

		List<IdentificationCacheDataPacket<TemperatureRecordDataCache>> list = cacheController.getTemperatureRecord(sourceId, queryStart, queryEnd);
		return new SolarThingTemperatureRecordQuery(list);
	}

}
