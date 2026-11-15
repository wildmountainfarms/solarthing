package me.retrodaredevil.solarthing.commands.event;

import me.retrodaredevil.solarthing.PacketTestUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class SecurityEventPacketTest {

	@Test
	void test() {
		PacketTestUtil.testJson(new ImmutableSecurityAcceptPacket("document-id-asdf"), SecurityEventPacket.class);
		PacketTestUtil.testJson(new ImmutableSecurityRejectPacket("document-id-asdf", SecurityRejectPacket.Reason.INVALID_DATA, "lmao invalid data"), SecurityEventPacket.class);
	}

}
