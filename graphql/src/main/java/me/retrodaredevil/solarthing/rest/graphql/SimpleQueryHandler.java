package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.database.MillisDatabase;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.NotFoundSolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.closed.meta.DefaultMetaDatabase;
import me.retrodaredevil.solarthing.type.closed.meta.EmptyMetaDatabase;
import me.retrodaredevil.solarthing.type.closed.meta.MetaDatabase;
import me.retrodaredevil.solarthing.packets.collection.*;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParsingErrorHandler;
import me.retrodaredevil.solarthing.rest.exceptions.DatabaseException;
import me.retrodaredevil.solarthing.type.closed.meta.RootMetaPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

public class SimpleQueryHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleQueryHandler.class);
	private static final Duration METADATA_CACHE_VALID = Duration.ofSeconds(15);

	private final DefaultInstanceOptions defaultInstanceOptions;

	private final SolarThingDatabase database;

	private VersionedPacket<RootMetaPacket> metadataCache = null;
	private Long lastMetadataCacheNanos = null;

	public SimpleQueryHandler(DefaultInstanceOptions defaultInstanceOptions, CouchDbDatabaseSettings couchDbDatabaseSettings, ObjectMapper objectMapper) {
		this.defaultInstanceOptions = defaultInstanceOptions;
		CouchDbInstance instance = CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties());
		// Notice that objectMapper is likely not lenient, but that's OK. We expect the user to keep this program up to date
		database = new CouchDbSolarThingDatabase(instance, PacketParsingErrorHandler.DO_NOTHING, objectMapper);
	}

	/**
	 * Converts a list of {@link InstancePacketGroup}s to merged {@link FragmentedPacketGroup}s.
	 */
	public List<? extends FragmentedPacketGroup> sortPackets(List<? extends InstancePacketGroup> packets, String sourceId) {
		if (packets.isEmpty()) {
			return Collections.emptyList();
		}
		if (sourceId == null) {
			return PacketGroups.mergePackets(
					PacketGroups.parseToInstancePacketGroups(packets, defaultInstanceOptions),
					SolarThingConstants.STANDARD_MAX_TIME_DISTANCE.toMillis(), SolarThingConstants.STANDARD_MASTER_ID_IGNORE_DISTANCE.toMillis()
			);
		}
		return PacketGroups.sortPackets(
				packets,
				defaultInstanceOptions,
				SolarThingConstants.STANDARD_MAX_TIME_DISTANCE.toMillis(), SolarThingConstants.STANDARD_MASTER_ID_IGNORE_DISTANCE.toMillis()
		).getOrDefault(sourceId, Collections.emptyList());
	}
	/**
	 *
	 * @param from The date millis from
	 * @param to The date millis to
	 * @param sourceId The source ID or null. If null, the returned List may contain packet groups from multiple sources
	 * @return The resulting packets
	 */
	private List<? extends InstancePacketGroup> queryPackets(MillisDatabase database, long from, long to, String sourceId) {
		final List<? extends PacketGroup> rawPacketGroups;
		try {

			rawPacketGroups = database.query(new MillisQueryBuilder()
					.startKey(from)
					.endKey(to)
					.build());
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException("Exception querying from " + from + " to " + to, e);
		}
		if(rawPacketGroups.isEmpty()){
			if (to - from > 60 * 1000) {
				// Only debug this message if the requester is actually asking for a decent chunk of data
				LOGGER.debug("No packets were queried between " + from + " and " + to);
			}
			return Collections.emptyList();
		}
		if (sourceId == null) {
			return PacketGroups.parseToInstancePacketGroups(rawPacketGroups, defaultInstanceOptions);
		}
		Map<String, List<InstancePacketGroup>> map = PacketGroups.parsePackets(rawPacketGroups, defaultInstanceOptions);
		if(map.containsKey(sourceId)){
			List<InstancePacketGroup> instancePacketGroupList = map.get(sourceId);
			return PacketGroups.orderByFragment(instancePacketGroupList);
		}
		throw new NoSuchElementException("No element with sourceId: '" + sourceId + "' available keys are: " + map.keySet());
	}
	public List<? extends InstancePacketGroup> queryStatus(long from, long to, String sourceId) {
		return queryPackets(database.getStatusDatabase(), from, to, sourceId);
	}
	public List<? extends InstancePacketGroup> queryEvent(long from, long to, String sourceId) {
		return queryPackets(database.getEventDatabase(), from, to, sourceId);
	}

	public MetaDatabase queryMeta() {
		final VersionedPacket<RootMetaPacket> metadata;
		synchronized (this) {
			final VersionedPacket<RootMetaPacket> currentCache = metadataCache;
			final Long lastMetadataCacheNanos = this.lastMetadataCacheNanos;
			if (lastMetadataCacheNanos != null && System.nanoTime() - lastMetadataCacheNanos < METADATA_CACHE_VALID.toNanos()) {
				requireNonNull(currentCache);
				metadata = currentCache;
			} else {
				UpdateToken updateToken = currentCache == null ? null : currentCache.getUpdateToken();
				final VersionedPacket<RootMetaPacket> newMetadata;
				try {
					newMetadata = database.queryMetadata(updateToken);
				} catch (NotFoundSolarThingDatabaseException e) {
					// If we have not defined metadata, then we return an "empty" instance
					return EmptyMetaDatabase.getInstance();
				} catch (SolarThingDatabaseException e) {
					throw new DatabaseException("Could not query meta", e);
				}
				this.lastMetadataCacheNanos = System.nanoTime();
				if (newMetadata == null) {
					requireNonNull(currentCache);
					metadata = currentCache;
				} else {
					metadataCache = newMetadata;
					metadata = newMetadata;
				}
			}
		}
		return new DefaultMetaDatabase(metadata.getPacket());
	}

	public List<VersionedPacket<StoredAlterPacket>> queryAlter(@NotNull String sourceId) {
		requireNonNull(sourceId);
		try {
			return database.getAlterDatabase().queryAll(sourceId);
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException("Could not query alter packets", e);
		}
	}

	public DefaultInstanceOptions getDefaultInstanceOptions() {
		return defaultInstanceOptions;
	}
}
