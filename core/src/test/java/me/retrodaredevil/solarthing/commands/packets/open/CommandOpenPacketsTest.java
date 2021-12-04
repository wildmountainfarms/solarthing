package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.database.couchdb.RevisionUpdateToken;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;
import me.retrodaredevil.solarthing.type.alter.flag.TimeRangeActivePeriod;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandData;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;
import me.retrodaredevil.solarthing.util.TimeRange;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

class CommandOpenPacketsTest {

	@Test
	void test() throws JsonProcessingException {
		PacketTestUtil.testJson(new ImmutableRequestCommandPacket("GEN OFF"), CommandOpenPacket.class, true);
		PacketTestUtil.testJson(
				new ImmutableRequestFlagPacket(
						new FlagData("disable_automations",
								new TimeRangeActivePeriod(TimeRange.create(Instant.parse("2021-10-05T04:53:22.877307Z"), Instant.parse("2021-10-05T04:53:44.305146Z")))
						)
				),
				CommandOpenPacket.class, true
		);
		PacketTestUtil.testJson(new ImmutableScheduleCommandPacket(new ScheduledCommandData(System.currentTimeMillis(), "GEN OFF", Collections.singleton(1)), UUID.randomUUID()), CommandOpenPacket.class, true);
		PacketTestUtil.testJson(new ImmutableDeleteAlterPacket("my_document_id", new RevisionUpdateToken("46-9ab7d71841380a36e1ec9e367deae36e")), CommandOpenPacket.class, true);
		PacketTestUtil.testJson(new ImmutableRequestHeartbeatPacket(new HeartbeatData("Hourly Mate Ping", "heartbeat.ping.mate.hourly", Duration.ofHours(1), Duration.ofMinutes(5)), UUID.randomUUID()), CommandOpenPacket.class, true);
	}
}
