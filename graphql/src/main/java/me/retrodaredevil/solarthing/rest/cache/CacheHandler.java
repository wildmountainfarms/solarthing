package me.retrodaredevil.solarthing.rest.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.request.BulkGetRequest;
import me.retrodaredevil.couchdbjava.response.BulkGetResponse;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.cache.CacheUtil;
import me.retrodaredevil.solarthing.cache.packets.CacheDataPacket;
import me.retrodaredevil.solarthing.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.cache.packets.IdentificationCacheNode;
import me.retrodaredevil.solarthing.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CacheHandler {
	private static final int QUERY_PERIOD_COUNT = 4 * 24; // we can request data a day at a time, but we won't do more than that
	/** This duration represents the amount of time to go "backwards" for calculating data for a single given period. If this is 4 and the period is from 10:00 to 11:00, then
	 * that period actually requires data from 6:00 to 11:00 */
	public static final Duration INFO_DURATION = Duration.ofHours(4);
	private static final List<CacheCreator> CACHE_CREATORS = Arrays.asList(
			new DefaultIdentificationCacheCreator<>(new ChargeControllerAccumulationCacheNodeCreator())
	);
	private final Duration duration = Duration.ofMinutes(15);

	private final ObjectMapper mapper;
	private final DefaultInstanceOptions defaultInstanceOptions;
	private final SolarThingDatabase database;
	private final CouchDbDatabase cacheDatabase;

	public CacheHandler(ObjectMapper mapper, DefaultInstanceOptions defaultInstanceOptions, CouchDbInstance couchDbInstance) {
		this.mapper = mapper;
		this.defaultInstanceOptions = defaultInstanceOptions;
		database = CouchDbSolarThingDatabase.create(couchDbInstance);

		cacheDatabase = couchDbInstance.getDatabase(SolarThingConstants.CACHE_UNIQUE_NAME);
	}

	public Duration getDuration() {
		return duration;
	}
	public long getPeriodNumber(long dateMillis) {
		return dateMillis / duration.toMillis();
	}
	private Instant getPeriodStartFromNumber(long periodNumber) {
		return Instant.ofEpochMilli(periodNumber * duration.toMillis());
	}
	private Instant getPeriodStartFromMillis(long dateMillis) {
		return Instant.ofEpochMilli(getPeriodNumber(dateMillis) * duration.toMillis());
	}
	public List<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> getChargeControllerAccumulation(String sourceId, long startPeriodNumber, long endPeriodNumber) {
		if (endPeriodNumber - startPeriodNumber + 1 <= QUERY_PERIOD_COUNT) {
			return getChargeControllerAccumulationRaw(sourceId, startPeriodNumber, endPeriodNumber);
		}
		List<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> r = new ArrayList<>();
		for (long periodNumber = startPeriodNumber; periodNumber <= endPeriodNumber; ) {
			long start = periodNumber;
			periodNumber += QUERY_PERIOD_COUNT;
			long end = Math.min(endPeriodNumber, periodNumber);
			r.addAll(getChargeControllerAccumulationRaw(sourceId, start, end));
		}
		return r;
	}
	private List<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> getChargeControllerAccumulationRaw(String sourceId, long startPeriodNumber, long endPeriodNumber) {
		final TypeReference<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> typeReference = new TypeReference<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>>() {};
		List<String> documentIds = new ArrayList<>();
		Map<String, Long> documentIdPeriodNumberMap = new HashMap<>();

		for (long periodNumber = startPeriodNumber; periodNumber <= endPeriodNumber; periodNumber++) {
			Instant periodStart = getPeriodStartFromNumber(periodNumber);
			String documentId = CacheUtil.getDocumentId(periodStart, duration, sourceId, ChargeControllerAccumulationDataCache.CACHE_NAME);
			documentIds.add(documentId);
			documentIdPeriodNumberMap.put(documentId, periodNumber);
		}
		BulkGetRequest request = BulkGetRequest.from(documentIds);
		final BulkGetResponse response;
		try {
			response = cacheDatabase.getDocumentsBulk(request);
		} catch (CouchDbException e) {
			throw new RuntimeException("Couldn't get documents", e);
		}
		Map<Long, IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> periodNumberPacketMap = new TreeMap<>();
		Long queryStartPeriodNumber = null;
		Long queryEndPeriodNumber = null;
		for (BulkGetResponse.Result result : response.getResults()) {
			if (result.hasConflicts()) {
				throw new IllegalStateException("There's a cache with a conflict! We don't know how to handle that! doc id: " + result.getDocumentId());
			}
			Long periodNumber = documentIdPeriodNumberMap.get(result.getDocumentId());
			if (periodNumber == null) {
				throw new IllegalStateException("Could not get period number for doc id: " + result.getDocumentId() + ". This should never happen.");
			}
			if (result.isError()) {
				if (queryStartPeriodNumber == null) {
					queryStartPeriodNumber = periodNumber;
					queryEndPeriodNumber = periodNumber;
				} else {
					queryStartPeriodNumber = Math.min(queryStartPeriodNumber, periodNumber);
					queryEndPeriodNumber = Math.max(queryEndPeriodNumber, periodNumber);
				}
			} else {
				JsonData jsonData = result.getJsonDataAssertNotConflicted();
				final IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache> value;
				try {
					value = CouchDbJacksonUtil.readValue(mapper, jsonData, typeReference);
				} catch (JsonProcessingException e) {
					throw new RuntimeException("TODO: eventually getting an error like this should be the same as result.isError() check above", e);
				}
				periodNumberPacketMap.put(periodNumber, value);
			}
		}
		if (queryStartPeriodNumber != null) {
			List<CacheDataPacket> calculatedPackets = calculatePeriod(queryStartPeriodNumber, queryEndPeriodNumber);
			// TODO throw these into CouchDB ^

			for (CacheDataPacket cacheDataPacket : calculatedPackets) {
				if (cacheDataPacket.getCacheName().equals(ChargeControllerAccumulationDataCache.CACHE_NAME)) {
					@SuppressWarnings("unchecked")
					IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache> packet = (IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>) cacheDataPacket;
					long periodNumber = getPeriodNumber(packet.getPeriodStartDateMillis());
					periodNumberPacketMap.put(periodNumber, packet);
				}
			}
		}

		return new ArrayList<>(periodNumberPacketMap.values());
	}
	private List<CacheDataPacket> calculatePeriod(long startPeriodNumber, long endPeriodNumber) {
		Instant firstPeriodStart = getPeriodStartFromNumber(startPeriodNumber);
		Instant lastPeriodEnd = getPeriodStartFromNumber(endPeriodNumber).plus(duration);
		Instant queryStart = firstPeriodStart.minus(INFO_DURATION);

		MillisQuery millisQuery = new MillisQueryBuilder()
				.startKey(queryStart.toEpochMilli())
				.endKey(lastPeriodEnd.toEpochMilli())
				.inclusiveEnd(false)
				.build();
		final List<PacketGroup> packetGroups;
		try {
			packetGroups = database.getStatusDatabase().query(millisQuery);
		} catch (SolarThingDatabaseException e) {
			// TODO the consumers of this API may be ok if there are holes in the data rather than getting no data at all, so maybe change this later?
			throw new RuntimeException("Couldn't query status packets", e);
		}
		List<CacheDataPacket> r = new ArrayList<>();
		Map<String, List<InstancePacketGroup>> sourceMap = PacketGroups.parsePackets(packetGroups, defaultInstanceOptions);
		for (Map.Entry<String, List<InstancePacketGroup>> entry : sourceMap.entrySet()) {
			String sourceId = entry.getKey();
			List<InstancePacketGroup> packets = entry.getValue();

			for (long periodNumber = startPeriodNumber; periodNumber <= endPeriodNumber; periodNumber++) {
				Instant periodStart = getPeriodStartFromNumber(periodNumber);
				for (CacheCreator creator : CACHE_CREATORS) {
					r.add(creator.createFrom(sourceId, packets, periodStart, duration));
				}
			}
		}
		return r;
	}


	public interface IdentificationCacheExporter<T> {
		IdentificationCacheNode<?> export(IdentifierFragment identifierFragment, List<InstancePacketGroup> packets, Instant periodStart, Duration periodDuration);
	}

}
