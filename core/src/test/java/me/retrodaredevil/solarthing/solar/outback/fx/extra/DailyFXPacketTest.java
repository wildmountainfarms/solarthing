package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.solar.outback.fx.common.ImmutableFXDailyData;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyFXPacketTest {
	@Test
	void test() throws JsonProcessingException {
		DailyFXPacket packet = new ImmutableDailyFXPacket(new ImmutableFXDailyData(0, System.currentTimeMillis(), 22, 22.3f, 0, 0, 0, 0, Collections.emptySet(), 0, 0, 0, Collections.emptySet()));
		assertEquals(22, packet.getDailyMinBatteryVoltage());
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String json = mapper.writeValueAsString(packet);
		System.out.println(json);
		DailyFXPacket output = mapper.readValue(json, DailyFXPacket.class);
		String json2 = mapper.writeValueAsString(output);
		assertEquals(json, json2);
	}
}
