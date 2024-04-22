package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.database.MillisDatabase;
import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.NotFoundSolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParsingErrorHandler;
import me.retrodaredevil.solarthing.rest.exceptions.DatabaseException;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.closed.meta.DefaultMetaDatabase;
import me.retrodaredevil.solarthing.type.closed.meta.EmptyMetaDatabase;
import me.retrodaredevil.solarthing.type.closed.meta.MetaDatabase;
import me.retrodaredevil.solarthing.type.closed.meta.RootMetaPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.requireNonNull;

public class SimpleQueryHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleQueryHandler.class);
	private static final Duration METADATA_CACHE_VALID = Duration.ofSeconds(15);

	private final DefaultInstanceOptions defaultInstanceOptions;

	private final SolarThingDatabase database;

	private VersionedPacket<RootMetaPacket> metadataCache = null;
	private Long lastMetadataCacheNanos = null;

	private final Map<UniqueQuery, Future<? extends List<? extends PacketGroup>>> executingQueryMap = new HashMap<>();
	private final Lock executingQueryMutex = new ReentrantLock();

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
	private List<? extends InstancePacketGroup> queryPackets(MillisDatabase database, long from, long to, @Nullable String sourceId, @Nullable Integer fragmentId) {

		MillisQuery millisQuery = new MillisQueryBuilder()
				.startKey(from)
				.endKey(to)
				.build();
		UniqueQuery uniqueQuery = new UniqueQuery(database, millisQuery);

		final Future<? extends List<? extends PacketGroup>> future;
		{
			// Many times a Grafana dashboard will make many graphql requests with the same from and to parameters.
			//   Without this code, each graphql request would result in a separate request to the database.
			//   Most of the time, these requests are being executed at the same time.
			//   This piece of code takes advantage of the fact that we are requesting the same data at the same time.
			//   If we find a Future that is already executing for a given query, we wait for that to complete instead of performing a separate request.
			// This is sort of a caching mechanism, but it's a VERY temporary caching mechanism since data is not kept after is it queried.
			executingQueryMutex.lock();
			var currentFuture = executingQueryMap.get(uniqueQuery);
			if (currentFuture != null) {
				future = currentFuture;
				executingQueryMutex.unlock();
			} else {
				RunnableFuture<? extends List<? extends PacketGroup>> runnableFuture = new FutureTask<>(() -> {
					try {
						return database.query(millisQuery);
					} catch (SolarThingDatabaseException e) {
						throw new DatabaseException("Exception querying from " + from + " to " + to, e);
					}
				});
				executingQueryMap.put(uniqueQuery, runnableFuture);
				executingQueryMutex.unlock();
				runnableFuture.run();
				future = runnableFuture;
				executingQueryMutex.lock();
				executingQueryMap.remove(uniqueQuery);
				executingQueryMutex.unlock();
			}
		}

		final List<? extends PacketGroup> rawPacketGroups;
		try {
			rawPacketGroups = future.get();
		} catch (InterruptedException e) {
			throw new DatabaseException("Interrupted!", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new DatabaseException("Unknown execution exception", e);
		}

		if(rawPacketGroups.isEmpty()){
			if (to - from > 60 * 1000) {
				// Only debug this message if the requester is actually asking for a decent chunk of data
				LOGGER.debug("No packets were queried between " + from + " and " + to);
			}
			return Collections.emptyList();
		}
		// Note: Before 2024-04-05 this method would throw a NoSuchElementException if no packets were found under a given Source ID
		//   Additionally PacketGroups.orderByFragment() was used to order the result ONLY IF a Source ID was provided.
		//   This behavior of this method has changed since then, which may have unintended effects.
		//   I really don't know if the ordering by Fragment ID was necessary, and I also don't think we really need that exception to be thrown.
		return rawPacketGroups.stream()
				.map(packetGroup -> PacketGroups.parseToInstancePacketGroup(packetGroup, defaultInstanceOptions))
				.filter(instancePacketGroup -> sourceId == null || instancePacketGroup.getSourceId().equals(sourceId))
				.filter(instancePacketGroup -> fragmentId == null || instancePacketGroup.getFragmentId() == fragmentId)
				.toList();
	}
	public List<? extends InstancePacketGroup> queryStatus(long from, long to, @Nullable String sourceId, @Nullable Integer fragmentId) {
		return queryPackets(database.getStatusDatabase(), from, to, sourceId, fragmentId);
	}
	public List<? extends InstancePacketGroup> queryEvent(long from, long to, @Nullable String sourceId, @Nullable Integer fragmentId) {
		return queryPackets(database.getEventDatabase(), from, to, sourceId, fragmentId);
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

	private static class UniqueQuery {
		private final MillisDatabase millisDatabase;
		private final MillisQuery millisQuery;

		private UniqueQuery(MillisDatabase millisDatabase, MillisQuery millisQuery) {
			this.millisDatabase = millisDatabase;
			this.millisQuery = millisQuery;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			UniqueQuery that = (UniqueQuery) o;
			return millisDatabase.equals(that.millisDatabase) && millisQuery.equals(that.millisQuery);
		}

		@Override
		public int hashCode() {
			return Objects.hash(millisDatabase, millisQuery);
		}
	}
}
