package me.retrodaredevil.solarthing.database.cache;

import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.StoredIdentifier;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.util.TimeRange;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseCacheTest {

	@Test
	void test() {
		Instant instant = Instant.now();
		Clock clock = Clock.fixed(instant, ZoneOffset.UTC);
		SimpleDatabaseCache cache = SimpleDatabaseCache.createDefault(clock);
		assertTrue(cache.getAllCachedPackets().isEmpty());

		{
			MillisQuery firstRecommendedQuery = cache.getRecommendedQuery();
			assertEquals(instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).toEpochMilli(), firstRecommendedQuery.getStartKey());
			assertEquals(instant.toEpochMilli(), firstRecommendedQuery.getEndKey());

			cache.feed(Arrays.asList(
					create(instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(30)),
					create(instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(40)),
					create(instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(50)),
					create(instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).minusSeconds(20)),
					create(instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(20)),
					create(instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(40))
			), firstRecommendedQuery.getStartKey(), firstRecommendedQuery.getEndKey());
		}

		assertEquals(6, cache.getAllCachedPackets().size());
		assertEquals(
				6,
				cache.getCachedPacketsInRange(TimeRange.create(
						instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(30),
						instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(40)), false
				).size()
		);
		assertEquals(
				5,
				cache.getCachedPacketsInRange(TimeRange.create(
						instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(31),
						instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(40)), false
				).size()
		);
		assertEquals(
				4,
				cache.getCachedPacketsInRange(TimeRange.create(
						instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(31),
						instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(39)), false
				).size()
		);

		{
			MillisQuery millisQuery = cache.getRecommendedQuery();
			assertEquals(instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).toEpochMilli(), millisQuery.getStartKey());
			assertEquals(instant.toEpochMilli(), millisQuery.getEndKey());

			cache.feed(Arrays.asList(
					create(instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).minusSeconds(20)),
					create(instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(20)),
					create(instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(41)),
					create(instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(50))
			), millisQuery.getStartKey(), millisQuery.getEndKey());
		}
		assertEquals(7, cache.getAllCachedPackets().size());
		assertEquals(
				5,
				cache.getCachedPacketsInRange(TimeRange.create(
						instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(30),
						instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(40)), false
				).size()
		);
		assertEquals(
				6,
				cache.getCachedPacketsInRange(TimeRange.create(
						instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(30),
						instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(41)), false
				).size()
		);
		assertEquals(
				7,
				cache.getCachedPacketsInRange(TimeRange.create(
						instant.minus(SimpleDatabaseCache.DEFAULT_MINIMUM_DURATION).plusSeconds(30),
						instant.minus(SimpleDatabaseCache.DEFAULT_VOLATILE_WINDOW_DURATION).plusSeconds(50)), false
				).size()
		);
	}

	private static StoredPacketGroup create(Instant instant) {
		return create(instant.toEpochMilli());
	}
	private static StoredPacketGroup create(long dateMillis) {
		return PacketGroups.createStoredPacketGroup(Collections.emptyList(), dateMillis, new DateMillisStoredIdentifier(dateMillis));
	}

	private static class DateMillisStoredIdentifier implements StoredIdentifier {

		private final long dateMillis;

		private DateMillisStoredIdentifier(long dateMillis) {
			this.dateMillis = dateMillis;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			DateMillisStoredIdentifier that = (DateMillisStoredIdentifier) o;
			return dateMillis == that.dateMillis;
		}

		@Override
		public int hashCode() {
			return Objects.hash(dateMillis);
		}

		@Override
		public String toString() {
			return "DateMillisStoredIdentifier(" +
					"dateMillis=" + dateMillis +
					')';
		}
	}

}
