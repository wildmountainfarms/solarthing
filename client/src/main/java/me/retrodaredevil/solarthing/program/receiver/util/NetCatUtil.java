package me.retrodaredevil.solarthing.program.receiver.util;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.StringUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

@UtilityClass
@NullMarked
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
			return value.asString();
		}
		String value = split[1];
		ObjectReader reader = MAPPER.readerForUpdating(write);
		ObjectNode input = new ObjectNode(JsonNodeFactory.instance);
		input.put(fieldName, value);
		try {
			reader.readValue(input);
		} catch (JacksonException e) {
			return "Got error: " + e.getMessage();
		}
		return "success";
	}
}
