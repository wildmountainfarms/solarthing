package me.retrodaredevil.solarthing.database.cache;

import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.util.TimeRange;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;

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

	private static PacketGroup create(Instant instant) {
		return create(instant.toEpochMilli());
	}
	private static PacketGroup create(long dateMillis) {
		return PacketGroups.createPacketGroup(Collections.emptyList(), dateMillis);
	}

}
