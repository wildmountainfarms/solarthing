package me.retrodaredevil.solarthing.misc.error;

import me.retrodaredevil.solarthing.PacketTestUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public class ErrorPacketTest {

	@Test
	void test() {
		{
			ExceptionErrorPacket packet = new ImmutableExceptionErrorPacket("java.lang.Exception", "My message", "Code location 1", "Instance 1");
			assertEquals("java.lang.Exception", packet.getExceptionName());
			PacketTestUtil.testJson(packet, ExceptionErrorPacket.class);
			PacketTestUtil.testJson(packet, ErrorPacket.class);
		}
	}
}
