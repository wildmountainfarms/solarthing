package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TracerTest {
	@Test
	void test() throws JsonProcessingException {
		TracerStatusPacket packet = new ImmutableTracerStatusPacket(
				null, 100, 20, 200, 24, 20, 200,
				0, 20, 0.0f, 0.0f, 0.0f,
				12.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 25.0f, 26.0f, 26.0f,
				50, 0.0f, 12, 0, 0, 0.0f,
				0.0f, 0.0f, 11.9f, 0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 25.0f, 25.0f, 0, 300, 3,
				15.0f, 14.8f, 14.5f, 14.0f, 13.8f, 13.2f, 12.8f,
				11.8f, 11.8f, 11.8f, 11.2f, 11.8f, 14297963433015L,
				30, 65.0f, -40.0f, 85.0f, 75.0f, 85.0f, 75.0f,
				0, 3.0f, 10, 6.0f, 10, 0, 256, 256,
				81604378624L, 25769803776L, 81604378624L, 25769803776L,
				524, 0, 0, false, 120, 120, 30, 100,
				0, false, false, false, false, true
		);

		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String json = mapper.writeValueAsString(packet);
		System.out.println(json);
		TracerStatusPacket parsed = mapper.readValue(json, TracerStatusPacket.class);

		String json2 = mapper.writeValueAsString(parsed);
		assertEquals(json, json2);
	}
}
