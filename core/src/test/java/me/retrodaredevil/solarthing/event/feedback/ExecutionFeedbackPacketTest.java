package me.retrodaredevil.solarthing.event.feedback;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.open.OpenSource;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionFeedbackPacketTest {

	@Test
	void test() throws JsonProcessingException {
		PacketTestUtil.testJson(
				new ImmutableExecutionFeedbackPacket(
						"hello there",
						"important.informational",
						new OpenSourceExecutionReason(new OpenSource("josh", 1632366551290L, new ImmutableRequestCommandPacket("GEN OFF"), "GEN OFF"))
				),
				ExecutionFeedbackPacket.class
		);
	}

}
