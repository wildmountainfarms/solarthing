package me.retrodaredevil.solarthing.type.cache.packets.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BatteryRecordDataCacheTest {

	@Test
	void testDeserialize() throws JsonProcessingException {
		BatteryRecordDataCache cache = new BatteryRecordDataCache(
				new OutbackIdentifier(1),
				Instant.parse("2022-02-16T00:29:55Z").toEpochMilli(),
				Instant.parse("2022-02-16T00:44:52Z").toEpochMilli(),
				null,
				new BatteryRecordDataCache.Record(
						23.9f, Instant.parse("2022-02-16T00:37:34Z").toEpochMilli(),
						24.4f, Instant.parse("2022-02-16T00:39:31Z").toEpochMilli(),
						0.0, 0,
						24.1 * 0.25, Instant.parse("2022-02-16T00:44:52Z").toEpochMilli() - Instant.parse("2022-02-16T00:29:55Z").toEpochMilli(),
						0.0, 0L
				)
		);
		PacketTestUtil.testJson(cache, BatteryRecordDataCache.class);
	}
}
