package me.retrodaredevil.solarthing.solar.tracer;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.MonthDay;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TracerUtilTest {

	@Test
	void test() {
		int yearNumber = 97;
		MonthDay monthDay = MonthDay.of(5, 22);
		LocalTime localTime = LocalTime.of(14, 58, 15);
		long raw = 15 | (58 << 8) | (14 << 16) | (22 << 24) | (5L << 32L) | (97L << 40L);
		assertEquals(yearNumber, TracerUtil.extractTracer48BitRawInstantToYearNumber(raw));
		assertEquals(monthDay, TracerUtil.extractTracer48BitRawInstantToMonthDay(raw));
		assertEquals(localTime, TracerUtil.extractTracer48BitRawInstantToLocalTime(raw));

		assertEquals(raw, TracerUtil.convertInstantToTracer48BitRaw(yearNumber, monthDay, localTime));
	}
}
