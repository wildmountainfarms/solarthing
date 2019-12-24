package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmutableFXACModeChangePacketTest {
	@Test
	void testJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = "{ \"packetType\": \"FX_AC_MODE_CHANGE\", \"address\": 1, \"acModeValue\": 2, \"previousACModeValue\": null }";
		FXACModeChangePacket packet = mapper.readValue(json, FXACModeChangePacket.class);
		assertTrue(packet instanceof ImmutableFXACModeChangePacket);
		assertEquals(1, ((OutbackIdentifier) packet.getIdentifier().getSupplementaryTo()).getAddress());
		assertEquals(2, packet.getACModeValue());
		assertNull(packet.getPreviousACModeValue());

		String output = mapper.writer().writeValueAsString(packet);
		System.out.println(output);
		FXACModeChangePacket packet2 = mapper.readValue(output, FXACModeChangePacket.class);
		assertTrue(packet2 instanceof ImmutableFXACModeChangePacket);
		assertEquals(1, ((OutbackIdentifier) packet2.getIdentifier().getSupplementaryTo()).getAddress());
		assertEquals(2, packet2.getACModeValue());
		assertNull(packet2.getPreviousACModeValue());
	}
}
