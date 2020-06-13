package me.retrodaredevil.solarthing.commands.packets;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.commands.CommandInfo;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsPacket;
import me.retrodaredevil.solarthing.commands.packets.status.CommandStatusPacket;
import me.retrodaredevil.solarthing.commands.packets.status.ImmutableAvailableCommandsPacket;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CommandStatusPacketTest {

	@Test
	void test() throws JsonProcessingException {
		AvailableCommandsPacket packet = new ImmutableAvailableCommandsPacket(Collections.singletonList(new CommandInfo("asdf", "Asdf", "Cool")));
		assertEquals("asdf", packet.getCommandInfoList().get(0).getName());
		assertEquals(1, packet.getCommandInfoList().size());
		PacketTestUtil.testJson(packet, AvailableCommandsPacket.class);
		PacketTestUtil.testJson(packet, CommandStatusPacket.class);
	}
}
