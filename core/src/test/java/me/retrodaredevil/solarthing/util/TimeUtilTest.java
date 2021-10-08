package me.retrodaredevil.solarthing.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilTest {

	@Test
	void test() {
		assertEquals("PT1H", TimeUtil.informalDurationToFormal("1 hour"));
		assertEquals("PT2H", TimeUtil.informalDurationToFormal("2 hours"));
		assertEquals("PT2H", TimeUtil.informalDurationToFormal("2hours"));
		assertEquals("PT2H5M", TimeUtil.informalDurationToFormal("2hours 5 minutes"));
		assertEquals("PT2H5M", TimeUtil.informalDurationToFormal("T2hours 5 minutes"));
		assertEquals("PT2H5M", TimeUtil.informalDurationToFormal("PT2hours 5 minutes"));

		assertEquals(Duration.ofHours(2), TimeUtil.lenientParseDurationOrNull("2 hours"));
		assertNull(TimeUtil.lenientParseDurationOrNull("hours"));
	}
}
