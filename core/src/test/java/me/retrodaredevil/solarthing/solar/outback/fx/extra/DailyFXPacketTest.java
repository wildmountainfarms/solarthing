package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.solar.PacketTestUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.common.ImmutableFXDailyData;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyFXPacketTest {
	@Test
	void test() throws JsonProcessingException {
		DailyFXPacket packet = new ImmutableDailyFXPacket(new ImmutableFXDailyData(0, System.currentTimeMillis(), 22, 22.3f, 0, 0, 0, 0, Collections.emptySet(), 0, 0, 0, Collections.emptySet()));
		assertEquals(22, packet.getDailyMinBatteryVoltage());
		PacketTestUtil.testJson(packet, DailyFXPacket.class);
	}
}
