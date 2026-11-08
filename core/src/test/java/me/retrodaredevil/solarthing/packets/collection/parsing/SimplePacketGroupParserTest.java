package me.retrodaredevil.solarthing.packets.collection.parsing;

import me.retrodaredevil.solarthing.commands.event.SecurityEventPacket;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class SimplePacketGroupParserTest {
	@Test
	void reportsUnknownPacketAndContinuesWithKnownPackets() throws PacketParseException {
		ObjectMapper mapper = JacksonUtil.defaultMapperBuilder().registerSubtypes(InstancePacket.class).build();
		List<Exception> errors = new ArrayList<>();
		SimplePacketGroupParser parser = new SimplePacketGroupParser(mapper, errors::add);
		PacketGroup group = parser.parse((ObjectNode) mapper.readTree("""
				{"dateMillis":1,"packets":[
				  {"packetType":"UNKNOWN"},
				  {"packetType":"SOURCE","sourceId":"test-source"}
				]}
				"""));
		assertEquals(1, errors.size());
		assertInstanceOf(InvalidTypeIdException.class, errors.getFirst());
		assertEquals(1, group.getPackets().size());
		assertEquals("test-source", assertInstanceOf(InstanceSourcePacket.class, group.getPackets().getFirst()).getSourceId());
	}

	@Test
	void reportsMalformedKnownPacket() throws PacketParseException {
		ObjectMapper mapper = JacksonUtil.defaultMapperBuilder().registerSubtypes(SecurityEventPacket.class).build();
		List<Exception> errors = new ArrayList<>();
		SimplePacketGroupParser parser = new SimplePacketGroupParser(mapper, errors::add);
		PacketGroup group = parser.parse((ObjectNode) mapper.readTree("{\"dateMillis\":1,\"packets\":[{\"packetType\":\"ACCEPT\"}]}"));
		assertTrue(group.getPackets().isEmpty());
		assertEquals(1, errors.size());
		assertInstanceOf(JacksonException.class, errors.getFirst());
	}

	@Test
	void letsErrorHandlerRejectThePacketGroup() {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		PacketParseException rejection = new PacketParseException("Reject this group");
		SimplePacketGroupParser parser = new SimplePacketGroupParser(mapper, error -> { throw rejection; });
		ObjectNode group = (ObjectNode) mapper.readTree("{\"dateMillis\":1,\"packets\":[{\"packetType\":\"UNKNOWN\"}]}");
		assertSame(rejection, assertThrows(PacketParseException.class, () -> parser.parse(group)));
	}
}
