package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstancePacketTest {

	@Test
	void test() throws JsonProcessingException {

		InstanceFragmentIndicatorPacket fragmentPacket = InstanceFragmentIndicatorPackets.create(3);
		PacketTestUtil.testJson(fragmentPacket, InstanceFragmentIndicatorPacket.class);
		PacketTestUtil.testJson(fragmentPacket, InstancePacket.class);

		InstanceSourcePacket sourcePacket = InstanceSourcePackets.create("coolio");
		PacketTestUtil.testJson(sourcePacket, InstanceSourcePacket.class);
		PacketTestUtil.testJson(sourcePacket, InstancePacket.class);

		InstanceTargetPacket targetPacket = InstanceTargetPackets.create(Arrays.asList(1, 5, 9));
		assertTrue(targetPacket.isTarget(1));
		assertFalse(targetPacket.isTarget(2));
		PacketTestUtil.testJson(targetPacket, InstanceTargetPacket.class);
		PacketTestUtil.testJson(targetPacket, InstancePacket.class);
	}
}
