package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandOpenPacketTest {

	@Test
	void test() throws JsonProcessingException {
		RequestCommandPacket packet = new ImmutableRequestCommandPacket("GEN OFF");
		assertEquals("GEN OFF", packet.getCommandName());
		PacketTestUtil.testJson(packet, RequestCommandPacket.class);
		PacketTestUtil.testJson(packet, CommandOpenPacket.class);
	}
}
