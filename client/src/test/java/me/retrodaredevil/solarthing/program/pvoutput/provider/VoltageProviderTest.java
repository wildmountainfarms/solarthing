package me.retrodaredevil.solarthing.program.pvoutput.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider.AverageBatteryVoltageProvider;
import me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider.VoltageProvider;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoltageProviderTest {

	@Test
	void testDeserialize() throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String averageBatteryVoltageJson = "{\"type\": \"average-battery-voltage\"}";
		VoltageProvider voltageProvider = mapper.readValue(averageBatteryVoltageJson, VoltageProvider.class);
		assertTrue(voltageProvider instanceof AverageBatteryVoltageProvider);
	}
}
