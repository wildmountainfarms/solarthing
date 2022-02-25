package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityEventPacketTest {

	@Test
	void test() throws JsonProcessingException {
		PacketTestUtil.testJson(new ImmutableSecurityAcceptPacket("document-id-asdf"), SecurityEventPacket.class);
		PacketTestUtil.testJson(new ImmutableSecurityRejectPacket("document-id-asdf", SecurityRejectPacket.Reason.INVALID_DATA, "lmao invalid data"), SecurityEventPacket.class);
	}

}
