package me.retrodaredevil.solarthing.misc.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.misc.source.W1Source;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeatherPacketsTest {

	@Test
	void test() throws JsonProcessingException {
		{
			TemperaturePacket packet = new CelsiusTemperaturePacket(1, new W1Source("28-301a279ffb2"), 24.0f);
			assertEquals(24.0f, packet.getTemperatureCelsius());
			assertEquals("28-301a279ffb2", packet.getDeviceSource().getName());
			assertEquals(1, packet.getDataId());
			PacketTestUtil.testJson(packet, TemperaturePacket.class);
			PacketTestUtil.testJson(packet, WeatherPacket.class);
		}

	}
}
