package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PacketTestUtil {
	public static <T> void testJson(T originalPacket, Class<T> packetClass) throws JsonProcessingException {
		testJson(originalPacket, packetClass, false);
	}
	public static <T> void testJson(T originalPacket, Class<T> packetClass, boolean isEqualsImplemented) throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String json = mapper.writeValueAsString(originalPacket);
//		System.out.println(json);
		T output = mapper.readValue(json, packetClass);
		String json2 = mapper.writeValueAsString(output);
		assertEquals(json, json2);
		if (isEqualsImplemented) {
			assertEquals(originalPacket, output, "equals() comparison failed for originalPacket=" + originalPacket);
		}
	}
}
