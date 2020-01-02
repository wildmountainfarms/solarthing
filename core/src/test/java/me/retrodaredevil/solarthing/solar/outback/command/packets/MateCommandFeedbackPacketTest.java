package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.solar.PacketTestUtil;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import org.junit.jupiter.api.Test;

//import static org.junit.jupiter.api.Assertions.*;

final class MateCommandFeedbackPacketTest {

	@Test
	void test() throws JsonProcessingException {
		PacketTestUtil.testJson(new ImmutableSuccessMateCommandPacket(MateCommand.AUX_OFF, "my_source"), MateCommandFeedbackPacket.class);
	}
}
