package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class FeedbackPacketTest {

	@Test
	void test() throws JsonProcessingException {
		PacketTestUtil.testJson(
				new ImmutableExecutionFeedbackPacket(
						"hello there",
						"important.informational",
						new OpenSourceExecutionReason(new OpenSource("josh", 1632366551290L, new ImmutableRequestCommandPacket("GEN OFF"), "GEN OFF"))
				),
				FeedbackPacket.class
		);
		PacketTestUtil.testJson(
				new ImmutableHeartbeatPacket(new HeartbeatData("hello", "hello.category", Duration.ofHours(1), Duration.ofMinutes(5)), new OpenSourceExecutionReason(new OpenSource("josh", 1632366551290L, new ImmutableRequestCommandPacket("GEN OFF"), "GEN OFF"))),
				FeedbackPacket.class
		);
	}

}
