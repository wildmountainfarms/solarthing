package me.retrodaredevil.solarthing.solar.pzem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PzemTest {
	@Test
	void test() throws JsonProcessingException {

		String json = "{\n" +
				"  \n" +
				"\"packetType\" : \"PZEM_SHUNT\",\n" +
				"  \"dataId\" : 1,\n" +
				"  \"voltageValueRaw\" : 25,\n" +
				"  \"currentValueRaw\" : 4,\n" +
				"  \"powerValueRaw\" : 100,\n" +
				"  \"energyValueRaw\" : null,\n" +
				"  \"highVoltageAlarmStatus\" : 0,\n" +
				"  \"lowVoltageAlarmStatus\" : 0,\n" +
				"  \"modbusAddress\" : 1\n" +
				"}";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		PzemShuntStatusPacket parsedPacket = mapper.readValue(json, PzemShuntStatusPacket.class);
		String generatedJson = mapper.writeValueAsString(parsedPacket);
		PzemShuntStatusPacket reparsedPacket = mapper.readValue(generatedJson, PzemShuntStatusPacket.class);
		for (PzemShuntStatusPacket packet : new PzemShuntStatusPacket[] { parsedPacket, reparsedPacket}) {
			assertEquals(1, packet.getDataId());
			assertEquals(25, packet.getVoltageValueRaw());
			assertEquals(4, packet.getCurrentValueRaw());
			assertEquals(100, packet.getPowerValueRaw());
			assertNull(packet.getEnergyValueRaw());
			assertEquals(0, packet.getHighVoltageAlarmStatus());
			assertEquals(0, packet.getLowVoltageAlarmStatus());
			assertFalse(packet.isHighVoltageAlarm());
			assertFalse(packet.isLowVoltageAlarm());
		}
	}
}
