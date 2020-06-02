package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DevicePacketTest {

	@Test
	void test() throws JsonProcessingException {
		{
			CpuTemperaturePacket packet = new CelsiusCpuTemperaturePacket(20);
			assertEquals(20, packet.getCpuTemperatureCelsius());
			PacketTestUtil.testJson(packet, CpuTemperaturePacket.class);
			PacketTestUtil.testJson(packet, DevicePacket.class);
		}
	}
}
