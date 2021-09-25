package me.retrodaredevil.solarthing.database.cache;

import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.util.TimeRange;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Note: Not thread safe
 */
public class SimpleDatabaseCache implements DatabaseCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDatabaseCache.class);

	public static final Duration DEFAULT_KEEP_MAX_DURATION = Duration.ofHours(31);
	public static final Duration DEFAULT_KEEP_DURATION = Duration.ofHours(30);
	public static final Duration DEFAULT_MINIMUM_DURATION = Duration.ofHours(25);
	public static final Duration DEFAULT_VOLATILE_WINDOW_DURATION = Duration.ofMinutes(15);

	private final TreeSet<Node> packetGroups = new TreeSet<>();

	private final Duration keepMaxDuration;
	private final Duration keepDuration;
	private final Duration minimumDuration;
	private final Duration volatileWindowDuration;
	private final Clock clock;

	private Data data = null;

	public SimpleDatabaseCache(Duration keepMaxDuration, Duration keepDuration, Duration minimumDuration, Duration volatileWindowDuration, Clock clock) {
		this.keepMaxDuration = keepMaxDuration;
		this.keepDuration = keepDuration;
		this.minimumDuration = minimumDuration;
		this.volatileWindowDuration = volatileWindowDuration;
		this.clock = clock;
	}
	public static SimpleDatabaseCache createDefault() {
		return new SimpleDatabaseCache(DEFAULT_KEEP_MAX_DURATION, DEFAULT_KEEP_DURATION, DEFAULT_MINIMUM_DURATION, DEFAULT_VOLATILE_WINDOW_DURATION, Clock.systemUTC());
	}

	@Override
	public Stream<PacketGroup> createCachedPacketsInRangeStream(TimeRange timeRange, boolean descending) {
		NavigableSet<Node> set = packetGroups;
		if (timeRange.getStartTimeMillis() != null) {
			set = set.tailSet(new Node(timeRange.getStartTimeMillis()), true);
		}
		if (timeRange.getEndTimeMillis() != null) {
			set = set.headSet(new Node(timeRange.getEndTimeMillis()), true);
		}
		if (descending) {
			set = set.descendingSet();
		}
		return set.stream()
				.map(node -> node.packetGroup);
	}

	public MillisQuery getRecommendedQuery() {
		Data data = this.data;
		Instant now = clock.instant();
		long minimumStart = now.minus(minimumDuration).toEpochMilli();
		long end = now.toEpochMilli();
		final long start;
		if (data == null || data.firstQueryStartDateMillis > minimumStart) {
			start = minimumStart;
		} else {
			start = Math.max(minimumStart, data.volatileAfterDateMillis);
		}

		return new MillisQueryBuilder()
				.startKey(start)
				.endKey(end)
				.inclusiveEnd(true)
				.build();
	}

	public void feed(List<PacketGroup> queriedPacketGroups, long queryStartDateMillis, long queryEndDateMillis) {
		if (queryStartDateMillis > queryEndDateMillis) {
			throw new IllegalArgumentException("start must be <= end! queryStartDateMillis: " + queryStartDateMillis + " queryEndDateMillis: " + queryEndDateMillis);
		}
		if (!packetGroups.isEmpty() && packetGroups.last().dateMillis > queryEndDateMillis) {
			throw new IllegalArgumentException("The query end date must never decrease! We have a packet group with a date millis after the passed queryEndDateMillis=" + queryEndDateMillis);
		}
		Instant now = clock.instant();
		if (queryEndDateMillis > now.toEpochMilli()) {
			throw new IllegalArgumentException("Cannot have an queryEndDateMillis in the future! queryEndDateMillis: " + queryEndDateMillis + " now: " + now);
		}
		packetGroups.tailSet(new Node(queryStartDateMillis), true).clear();
		queriedPacketGroups.stream().map(Node::new).collect(Collectors.toCollection(() -> packetGroups));

		if (data == null) {
			data = new Data();
			data.firstQueryStartDateMillis = queryStartDateMillis;
			data.volatileAfterDateMillis = Math.min(queryEndDateMillis, now.minus(volatileWindowDuration).toEpochMilli());
		} else {
			if (queryStartDateMillis <= data.volatileAfterDateMillis) {
				data.volatileAfterDateMillis = Math.max(
						data.volatileAfterDateMillis, // this currently should never be chosen, but put it here in case we change something in the future
						Math.min(queryEndDateMillis, now.minus(volatileWindowDuration).toEpochMilli())
				);
			} else {
				LOGGER.warn("The start date for the queried packets is AFTER the volatileAfterDateMillis timestamp. queryStartDateMillis: " + queryStartDateMillis + " volatileAfterDateMillis: " + data.volatileAfterDateMillis);
			}
		}


		checkThenMaybePurge(now);
	}

	private void checkThenMaybePurge(Instant now) {
		final Node firstNode;
		try {
			firstNode = packetGroups.first();
		} catch (NoSuchElementException ignored){
			return;
		}
		Instant purgeIfOnOrBeforeInstant = now.minus(keepMaxDuration);
		if (firstNode.dateMillis <= purgeIfOnOrBeforeInstant.toEpochMilli()) {
			purgeOnOrBefore(now.minus(keepDuration)); // if we're going to purge, purge a bunch at once. keepDuration is less than keepMaxDuration
		}
	}
	private void purgeOnOrBefore(Instant instant) {
		packetGroups.headSet(new Node(instant.toEpochMilli()), true).clear();
	}

	private static final class Data {
		private long firstQueryStartDateMillis;
		/** The date in millis where any packet after that is still considered volatile. This value only increases*/
		private long volatileAfterDateMillis;
	}
	private static final class Node implements Comparable<Node> {
		private final long dateMillis;
		private final PacketGroup packetGroup;

		private Node(long dateMillis) {
			this.dateMillis = dateMillis;
			packetGroup = null;
		}

		private Node(PacketGroup packetGroup) {
			dateMillis = packetGroup.getDateMillis();
			this.packetGroup = packetGroup;
		}

		@Override
		public int compareTo(@NotNull SimpleDatabaseCache.Node node) {
			return Long.compare(dateMillis, node.dateMillis);
		}
	}
}
