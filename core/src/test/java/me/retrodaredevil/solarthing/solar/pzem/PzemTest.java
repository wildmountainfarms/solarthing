package me.retrodaredevil.solarthing.solar.pzem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
public class PzemTest {
	@Test
	void test() throws JsonProcessingException {

		String json = """
				{
					"packetType" : "PZEM_SHUNT",
					"dataId" : 1,
					"voltageValueRaw" : 25,
					"currentValueRaw" : 4,
					"powerValueRaw" : 100,
					"energyValueRaw" : 123,
					"highVoltageAlarmStatus" : 0,
					"lowVoltageAlarmStatus" : 0,
					"modbusAddress" : 1
				}""";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		PzemShuntStatusPacket parsedPacket = mapper.readValue(json, PzemShuntStatusPacket.class);
		String generatedJson = mapper.writeValueAsString(parsedPacket);
		PzemShuntStatusPacket reparsedPacket = mapper.readValue(generatedJson, PzemShuntStatusPacket.class);
		for (PzemShuntStatusPacket packet : new PzemShuntStatusPacket[] { parsedPacket, reparsedPacket}) {
			assertEquals(1, packet.getDataId());
			assertEquals(25, packet.getVoltageValueRaw());
			assertEquals(4, packet.getCurrentValueRaw());
			assertEquals(100, packet.getPowerValueRaw());
			assertEquals(123, packet.getEnergyValueRaw());
			assertEquals(0, packet.getHighVoltageAlarmStatus());
			assertEquals(0, packet.getLowVoltageAlarmStatus());
			assertFalse(packet.isHighVoltageAlarm());
			assertFalse(packet.isLowVoltageAlarm());
		}
	}
}
