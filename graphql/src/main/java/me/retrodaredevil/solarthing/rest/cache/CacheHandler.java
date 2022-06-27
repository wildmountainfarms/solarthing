package me.retrodaredevil.solarthing.rest.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.request.BulkGetRequest;
import me.retrodaredevil.couchdbjava.request.BulkPostRequest;
import me.retrodaredevil.couchdbjava.response.BulkDocumentResponse;
import me.retrodaredevil.couchdbjava.response.BulkGetResponse;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.rest.cache.creators.BatteryRecordCacheNodeCreator;
import me.retrodaredevil.solarthing.type.cache.CacheUtil;
import me.retrodaredevil.solarthing.type.cache.packets.CacheDataPacket;
import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.rest.cache.creators.CacheCreator;
import me.retrodaredevil.solarthing.rest.cache.creators.ChargeControllerAccumulationCacheNodeCreator;
import me.retrodaredevil.solarthing.rest.cache.creators.DefaultIdentificationCacheCreator;
import me.retrodaredevil.solarthing.rest.cache.creators.FXAccumulationCacheNodeCreator;
import me.retrodaredevil.solarthing.rest.exceptions.DatabaseException;
import me.retrodaredevil.solarthing.rest.exceptions.UnexpectedResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class CacheHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheHandler.class);
	private static final int QUERY_PERIOD_COUNT = 4 * 24; // we can request data a day at a time, but we won't do more than that
	/** This duration represents the amount of time to go "backwards" for calculating data for a single given period. If this is 4 and the period is from 10:00 to 11:00, then
	 * that period actually requires data from 6:00 to 11:00 */
	public static final Duration INFO_DURATION = Duration.ofHours(4);
	private static final List<CacheCreator> CACHE_CREATORS = List.of(
			new DefaultIdentificationCacheCreator<>(new ChargeControllerAccumulationCacheNodeCreator()),
			new DefaultIdentificationCacheCreator<>(new FXAccumulationCacheNodeCreator()),
			new DefaultIdentificationCacheCreator<>(new BatteryRecordCacheNodeCreator())
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

		cacheDatabase = couchDbInstance.getDatabase(SolarThingConstants.CACHE_DATABASE);
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
	public long getMaxPeriodNumber() {
		long currentPeriodNumber = getPeriodNumber(System.currentTimeMillis());
		return currentPeriodNumber - 2; // We cannot get data from the current period, and we cannot get data from the previous period
	}
	public <T extends CacheDataPacket> List<T> getCachesFromDateMillis(TypeReference<T> typeReference, String cacheName, String sourceId, long startMillis, long endMillis) {
		if (endMillis < startMillis) {
			throw new IllegalArgumentException("endMillis cannot be less than startMillis! startMillis: " + startMillis + " endMillis: " + endMillis);
		}
		return getCaches(typeReference, cacheName, sourceId, getPeriodNumber(startMillis), getPeriodNumber(endMillis));
	}
	public <T extends CacheDataPacket> @NotNull List<T> getCaches(TypeReference<T> typeReference, String cacheName, String sourceId, long startPeriodNumber, long endPeriodNumber) {
		requireNonNull(typeReference);
		requireNonNull(cacheName);
		requireNonNull(sourceId, "The source cannot be null!");
		if (endPeriodNumber < startPeriodNumber) {
			throw new IllegalArgumentException("endPeriodNumber cannot be less than startPeriodNumber! startPeriodNumber: " + startPeriodNumber + " endPeriodNumber: " + endPeriodNumber);
		}

		long maxPeriodNumber = getMaxPeriodNumber();
		startPeriodNumber = Math.min(startPeriodNumber, maxPeriodNumber);
		endPeriodNumber = Math.min(endPeriodNumber, maxPeriodNumber);

		if (endPeriodNumber - startPeriodNumber + 1 <= QUERY_PERIOD_COUNT) {
			return queryOrCalculateCaches(typeReference, cacheName, sourceId, startPeriodNumber, endPeriodNumber);
		}
		List<T> r = new ArrayList<>();
		for (long periodNumber = startPeriodNumber; periodNumber <= endPeriodNumber; ) {
			long start = periodNumber;
			periodNumber += QUERY_PERIOD_COUNT;
			long end = Math.min(endPeriodNumber, periodNumber);
			periodNumber++; // increment periodNumber because we are going to fetch [start, end], and next time want to get [end + 1, ...]
			r.addAll(queryOrCalculateCaches(typeReference, cacheName, sourceId, start, end));
		}
		return r;
	}
	private String getRevisionFromJsonData(JsonData jsonData) {
		final JsonNode node;
		try {
			node = CouchDbJacksonUtil.getNodeFrom(jsonData);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Couldn't parse to type, so tried parsing to JsonNode. That failed too.", e);
		}
		if (!node.isObject()) {
			throw new RuntimeException("The parsed JsonNode is not an object! node: " + node);
		}
		ObjectNode objectNode = (ObjectNode) node;
		JsonNode revisionNode = objectNode.get("_rev");
		if (revisionNode == null) {
			throw new RuntimeException("No _rev field on returned JSON!");
		}
		if (!revisionNode.isTextual()) {
			throw new RuntimeException("The _rev field is not a string! revisionNode: " + revisionNode);
		}
		return revisionNode.asText();
	}
	private <T extends CacheDataPacket> List<T> queryOrCalculateCaches(TypeReference<T> typeReference, String cacheName, String sourceId, long startPeriodNumber, long endPeriodNumber) {
		List<String> documentIds = new ArrayList<>(); // the document IDs needed to return data
		Map<String, Long> documentIdPeriodNumberMap = new HashMap<>(); // a map from a document ID to a period number

		for (long periodNumber = startPeriodNumber; periodNumber <= endPeriodNumber; periodNumber++) {
			Instant periodStart = getPeriodStartFromNumber(periodNumber);
			String documentId = CacheUtil.getDocumentId(periodStart, duration, sourceId, cacheName);
			documentIds.add(documentId);
			documentIdPeriodNumberMap.put(documentId, periodNumber);
		}
		BulkGetRequest request = BulkGetRequest.from(documentIds);
		final BulkGetResponse response;
		try {
			response = cacheDatabase.getDocumentsBulk(request);
		} catch (CouchDbException e) {
			throw new DatabaseException("CouchDB exception | message: " + e.getMessage(), e);
		}
		Map<String, String> documentIdRevisionMapForUpdate = new HashMap<>(); // map for documents that need to be updated. The value represents the revision that needs to be used to update it
		Map<Long, T> periodNumberPacketMap = new TreeMap<>(); // Map for period number -> cached data. This helps us make sure we only return a single piece of data for each period
		Set<String> doNotUpdateDocumentIdsSet = new HashSet<>(); // Set for document IDs that we already have and do not need to be updated
		Long queryStartPeriodNumber = null;
		Long queryEndPeriodNumber = null;
		for (BulkGetResponse.Result result : response.getResults()) {
			if (result.hasConflicts()) {
				// This could theoretically happen, but it's extremely unlikely and can only happen if someone has some wacky
				//   replication going on with their databases.
				throw new UnexpectedResponseException("cache document with conflict! doc id: " + result.getDocumentId());
			}
			Long periodNumber = documentIdPeriodNumberMap.get(result.getDocumentId());
			if (periodNumber == null) {
				throw new IllegalStateException("Could not get period number for doc id: " + result.getDocumentId() + ". This should never happen.");
			}
			T value = null;
			if (!result.isError()) {
				JsonData jsonData = result.getJsonDataAssertNotConflicted();
				try {
					value = CouchDbJacksonUtil.readValue(mapper, jsonData, typeReference);
					if (value.getSourceId().equals(sourceId) && value.getCacheName().equals(cacheName)) {
						periodNumberPacketMap.put(periodNumber, value);
					}
					doNotUpdateDocumentIdsSet.add(value.getDbId());
				} catch (JsonProcessingException ex) {
					// If we are in this catch block, one of two things has happened:
					//   The JSON is invalid (unlikely), we updated how a given cache is serialized/deserialized, or we're dumb and never tested to see if the deserialization works.
					//   If the JSON is actually invalid, getRevisionFromJsonData will throw an exception. Otherwise, we need to calculate the given cache again
					String revision = getRevisionFromJsonData(jsonData);
					documentIdRevisionMapForUpdate.put(result.getDocumentId(), revision);
				}
			}
			if (value == null) {
				if (queryStartPeriodNumber == null) {
					queryStartPeriodNumber = periodNumber;
					queryEndPeriodNumber = periodNumber;
				} else {
					queryStartPeriodNumber = Math.min(queryStartPeriodNumber, periodNumber);
					queryEndPeriodNumber = Math.max(queryEndPeriodNumber, periodNumber);
				}
			}
		}
		if (queryStartPeriodNumber != null) {
			List<CacheDataPacket> calculatedPackets = calculatePeriod(queryStartPeriodNumber, queryEndPeriodNumber);

			List<JsonData> calculatedPacketsJsonDataList = new ArrayList<>();
			int updateAttemptCount = 0;
			for (CacheDataPacket packet : calculatedPackets) {
				if (doNotUpdateDocumentIdsSet.contains(packet.getDbId())) {
					continue;
				}
				if (!sourceId.equals(packet.getSourceId()) || !cacheName.equals(packet.getCacheName())) {
					// Although we should be able to update packets from other sources, the logic above only gets
					//   revisions for documents of sourceId.
					// Also, the doNotUpdateDocumentIdsSet does not contain docuent IDs from other sources, so
					//   most of the time we have no idea if we actually need to update a packet from another source ID, which
					//   leads to conflicts
					// This is the same with different cache names, we don't know which ones we need to update and which ones we cannot
					continue;
				}
				JsonData json;
				try {
					String revision = documentIdRevisionMapForUpdate.get(packet.getDbId());
					if (revision == null) {
						json = new StringJsonData(mapper.writeValueAsString(packet));
					} else {
						json = new StringJsonData(mapper.writeValueAsString(new DocumentRevisionWrapper(revision, packet)));
						updateAttemptCount++;
					}
				} catch (JsonProcessingException e) {
					throw new RuntimeException("Should be able to serialize!", e);
				}
				calculatedPacketsJsonDataList.add(json);
			}
			final List<BulkDocumentResponse> postResponse;
			try {
				postResponse = cacheDatabase.postDocumentsBulk(new BulkPostRequest(calculatedPacketsJsonDataList));
			} catch (CouchDbException e) {
				throw new DatabaseException("Could not update cache", e);
			}
			int successCount = 0;
			int failCount = 0;
			for (BulkDocumentResponse documentResponse : postResponse) {
				if (documentResponse.isOk()) {
					successCount++;
				} else {
					failCount++;
					LOGGER.info("Error: " + documentResponse.getError() + " reason: " + documentResponse.getReason() + " on id: " + documentResponse.getId());
				}
			}
			LOGGER.debug("(Cache updating) Success: " + successCount + " fail: " + failCount + ". Tried to update: " + updateAttemptCount);

			int numberOfWantedType = 0;
			for (CacheDataPacket cacheDataPacket : calculatedPackets) {
				if (cacheDataPacket.getSourceId().equals(sourceId) && cacheDataPacket.getCacheName().equals(cacheName)) {
					@SuppressWarnings("unchecked")
					T packet = (T) cacheDataPacket;
					Long periodNumber = documentIdPeriodNumberMap.get(cacheDataPacket.getDbId());
					if (periodNumber == null) {
						throw new NullPointerException("No period number for id: " + cacheDataPacket.getDbId());
					}
					periodNumberPacketMap.put(periodNumber, packet);
					numberOfWantedType++;
				}
			}
			LOGGER.debug("Calculated " + calculatedPackets.size() + " and " + numberOfWantedType + " were of type " + cacheName);
		} else {
			LOGGER.trace("Didn't have to get any data");
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
		final List<? extends PacketGroup> packetGroups;
		try {
			packetGroups = database.getStatusDatabase().query(millisQuery);
		} catch (SolarThingDatabaseException e) {
			// (Was TO-DO)
			// The consumers of this API may be ok if there are holes in the data rather than getting no data at all, so maybe change this later?
			throw new DatabaseException("Couldn't query status packets for period. startPeriodNumber: " + startPeriodNumber + " endPeriodNumber: " + endPeriodNumber + " firstPeriodStart: " + firstPeriodStart, e);
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
	@JsonExplicit
	private static class DocumentRevisionWrapper {
		private final String revision;
		private final Object data;

		private DocumentRevisionWrapper(String revision, Object data) {
			requireNonNull(this.revision = revision);
			requireNonNull(this.data = data);
		}

		@JsonProperty("_rev")
		public String getRevision() {
			return revision;
		}

		@JsonUnwrapped
		public Object getData() {
			return data;
		}
	}
}
