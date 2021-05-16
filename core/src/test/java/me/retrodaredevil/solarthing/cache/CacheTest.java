package me.retrodaredevil.solarthing.cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class CacheTest {
	@Test
	void test() {
		LocalDateTime time = LocalDateTime.of(2021, 5, 15, 13, 0, 0);
		Instant instant = time.toInstant(ZoneId.of("UTC").getRules().getOffset(time));
		assertEquals(1621083600000L, instant.toEpochMilli());

		Duration duration = Duration.ofMinutes(15);

		String documentId = CacheUtil.getDocumentId(instant, duration, "default", "thingThing");
		assertEquals("cache_2021-05-15T13:00:00Z_PT15M_default_thingThing", documentId);
	}

}
