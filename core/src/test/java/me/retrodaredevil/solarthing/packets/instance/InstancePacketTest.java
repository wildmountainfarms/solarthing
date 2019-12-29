package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstancePacketTest {

	@Test
	void test() throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();

		InstanceFragmentIndicatorPacket fragmentPacket = InstanceFragmentIndicatorPackets.create(3);
		String fragmentJson = mapper.writeValueAsString(fragmentPacket);
		InstanceFragmentIndicatorPacket deserializedFragment = mapper.readValue(fragmentJson, InstanceFragmentIndicatorPacket.class);
		String fragmentJson2 = mapper.writeValueAsString(deserializedFragment);
		assertEquals(fragmentJson, fragmentJson2);

		InstanceSourcePacket sourcePacket = InstanceSourcePackets.create("coolio");
		String sourceJson = mapper.writeValueAsString(sourcePacket);
		InstanceSourcePacket deserializedSource = mapper.readValue(sourceJson, InstanceSourcePacket.class);
		String sourceJson2 = mapper.writeValueAsString(deserializedSource);
		assertEquals(sourceJson, sourceJson2);
	}
}
