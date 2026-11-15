package me.retrodaredevil.solarthing.solar.outback.fx.event;

import me.retrodaredevil.solarthing.PacketTestUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class ImmutableFXACModeChangePacketTest {
	@Test
	void testJson() {
		ObjectMapper mapper = JsonMapper.builder().build();
		String json = "{ \"packetType\": \"FX_AC_MODE_CHANGE\", \"address\": 1, \"acModeValue\": 2, \"previousACModeValue\": null }";
		FXACModeChangePacket packet = mapper.readValue(json, FXACModeChangePacket.class);
		PacketTestUtil.testJson(packet, FXACModeChangePacket.class);
		assertTrue(packet instanceof ImmutableFXACModeChangePacket);
		assertEquals(1, packet.getIdentifier().getSupplementaryTo().getAddress());
		assertEquals(2, packet.getACModeValue());
		assertNull(packet.getPreviousACModeValue());

		String output = mapper.writer().writeValueAsString(packet);
		System.out.println(output);
		FXACModeChangePacket packet2 = mapper.readValue(output, FXACModeChangePacket.class);
		assertTrue(packet2 instanceof ImmutableFXACModeChangePacket);
		assertEquals(1, packet2.getIdentifier().getSupplementaryTo().getAddress());
		assertEquals(2, packet2.getACModeValue());
		assertNull(packet2.getPreviousACModeValue());
	}
}
