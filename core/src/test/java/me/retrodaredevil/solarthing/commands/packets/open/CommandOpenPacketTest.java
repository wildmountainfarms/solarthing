package me.retrodaredevil.solarthing.commands.packets.open;

import me.retrodaredevil.solarthing.PacketTestUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class CommandOpenPacketTest {

	@Test
	void test() {
		RequestCommandPacket packet = new ImmutableRequestCommandPacket("GEN OFF");
		assertEquals("GEN OFF", packet.getCommandName());
		PacketTestUtil.testJson(packet, RequestCommandPacket.class);
		PacketTestUtil.testJson(packet, CommandOpenPacket.class);
	}
}
