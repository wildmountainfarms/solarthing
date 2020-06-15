package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.misc.device.CelsiusCpuTemperaturePacket;
import me.retrodaredevil.solarthing.misc.device.CpuTemperaturePacket;
import me.retrodaredevil.solarthing.misc.device.DevicePacket;
import me.retrodaredevil.solarthing.misc.source.W1Source;
import me.retrodaredevil.solarthing.misc.weather.CelsiusTemperaturePacket;
import me.retrodaredevil.solarthing.misc.weather.TemperaturePacket;
import me.retrodaredevil.solarthing.misc.weather.WeatherPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiPacketConverterTest {

	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();
	@Test
	void test() throws JsonProcessingException, PacketParseException {
		JsonPacketParser parser = MultiPacketConverter.createFrom(
				MAPPER,
				DevicePacket.class, WeatherPacket.class
		);
		testPacket(parser, new CelsiusCpuTemperaturePacket(40.0f), CpuTemperaturePacket.class);
		testPacket(parser, new CelsiusTemperaturePacket(1, new W1Source("asdf"), 40.0f), TemperaturePacket.class);
	}
	void testPacket(JsonPacketParser parser, DocumentedPacket<?> packet, Class<? extends DocumentedPacket<?>> clazz) throws JsonProcessingException, PacketParseException {
		String json = MAPPER.writeValueAsString(packet);
		JsonNode jsonNode = MAPPER.readTree(json);
		assertTrue(clazz.isInstance(parser.parsePacket(jsonNode)));
	}
}
