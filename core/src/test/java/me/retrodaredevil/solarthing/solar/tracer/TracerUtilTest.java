package me.retrodaredevil.solarthing.solar.tracer;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TracerUtilTest {

	@Test
	void test() {
		assertEquals(
				LocalDateTime.of(2021, 5, 22, 14, 58, 15),
				TracerUtil.convertTracerRawToDateTime(58 << 8 | 15, 22 << 8 | 14, 2021 << 8 | 5)
		);
	}
}
