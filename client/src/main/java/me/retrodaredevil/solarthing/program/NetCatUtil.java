package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.StringUtil;

import java.io.IOException;

@UtilityClass
public final class NetCatUtil {
	private NetCatUtil() { throw new UnsupportedOperationException(); }

	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();


	public static @Nullable String handle(Object write, Packet packet, String request) {
		String[] split = StringUtil.terminalSplit(request);
		if (split.length == 0) {
			return null;
		}
		String fieldName = split[0];
		if (split.length == 1) {
			ObjectNode node = MAPPER.valueToTree(packet);
			JsonNode value = node.get(fieldName);
			if (value == null) {
				return "unknown field";
			}
			return value.asText();
		}
		String value = split[1];
		ObjectReader reader = MAPPER.readerForUpdating(write);
		ObjectNode input = new ObjectNode(JsonNodeFactory.instance);
		input.put(fieldName, value);
		try {
			reader.readValue(input);
		} catch (IOException e) {
			return "Got error: " + e.getMessage();
		}
		return "success";
	}
}
