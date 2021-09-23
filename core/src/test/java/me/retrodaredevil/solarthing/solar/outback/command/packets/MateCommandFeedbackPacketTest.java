package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import org.junit.jupiter.api.Test;

//import static org.junit.jupiter.api.Assertions.*;

final class MateCommandFeedbackPacketTest {

	@Test
	void test() throws JsonProcessingException {
		PacketTestUtil.testJson(new ImmutableSuccessMateCommandPacket(null, MateCommand.AUX_OFF, "my_source", null), MateCommandFeedbackPacket.class);
		PacketTestUtil.testJson(new ImmutableSuccessMateCommandPacket(SuccessMateCommandPacket.VERSION_LATEST, MateCommand.AUX_OFF, "my_source", null), MateCommandFeedbackPacket.class);

		{
			OpenSource source = new OpenSource("josh", 1632198320733L, new ImmutableRequestCommandPacket("GEN OFF"), "GEN OFF");
			PacketTestUtil.testJson(new ImmutableSuccessMateCommandPacket(SuccessMateCommandPacket.VERSION_LATEST, MateCommand.AUX_OFF, source.toDataSource().toString(), new OpenSourceExecutionReason(source)), MateCommandFeedbackPacket.class);
		}
	}
}
