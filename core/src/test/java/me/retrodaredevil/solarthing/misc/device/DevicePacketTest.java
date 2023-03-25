package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DevicePacketTest {

	@Test
	void test() throws JsonProcessingException {
		{
			CpuTemperaturePacket packet = new CelsiusCpuTemperaturePacket(null, 20, Collections.emptyList());
			assertEquals(20, packet.getCpuTemperatureCelsius());
			PacketTestUtil.testJson(packet, CpuTemperaturePacket.class);
			PacketTestUtil.testJson(packet, DevicePacket.class);
		}
		{
			CpuTemperaturePacket packet = new CelsiusCpuTemperaturePacket(CpuTemperaturePacket.VERSION_WITH_CORES, 20.0f, Collections.singletonList(new CelsiusCpuTemperaturePacket.CelsiusCore(0, 20.0f)));
			assertEquals(20, packet.getCpuTemperatureCelsius());
			assertEquals(1, packet.getCores().size());
			assertEquals(20.0f, packet.getCores().get(0).getTemperatureCelsius());
			assertEquals(0, packet.getCores().get(0).getNumber());
			PacketTestUtil.testJson(packet, CpuTemperaturePacket.class);
		}
	}
}
