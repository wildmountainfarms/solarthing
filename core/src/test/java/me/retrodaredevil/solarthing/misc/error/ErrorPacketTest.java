package me.retrodaredevil.solarthing.misc.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorPacketTest {

	@Test
	void test() throws JsonProcessingException {
		{
			ExceptionErrorPacket packet = new ImmutableExceptionErrorPacket("java.lang.Exception", "My message", "Code location 1", "Instance 1");
			assertEquals("java.lang.Exception", packet.getExceptionName());
			PacketTestUtil.testJson(packet, ExceptionErrorPacket.class);
			PacketTestUtil.testJson(packet, ErrorPacket.class);
		}
	}
}
