package me.retrodaredevil.solarthing.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helps you deserialize something by using a lenient mapper and throwing an exception if there
 * were unused properties.
 *
 * NOTE: This only works under the assumption that the values you read can be serialized and deserialized the same way.
 */
public class UnwrappedDeserializeHelper {
	private static final ObjectMapper MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());
	private final JsonParser parser;
	private final Class<?> clazz;
	private final ObjectNode objectNode;

	public UnwrappedDeserializeHelper(JsonParser parser, Class<?> clazz) throws IOException {
		this.parser = parser;
		this.clazz = clazz;
		this.objectNode = parser.readValueAsTree();
	}
	public <T> T readValue(Class<T> clazz) throws JsonProcessingException {
		T r = MAPPER.treeToValue(objectNode, clazz);
		for (Iterator<String> it = MAPPER.valueToTree(r).fieldNames(); it.hasNext(); ){
			objectNode.remove(it.next());
		}
		return r;
	}
	public void assertNoUnknown() throws MismatchedInputException {
		List<String> unknownNames = new ArrayList<>();
		for (Iterator<String> it = objectNode.fieldNames(); it.hasNext(); ){
			String unknownPropertyName = it.next();
			unknownNames.add(unknownPropertyName);
		}
		if (!unknownNames.isEmpty()) {
			throw MismatchedInputException.from(parser, clazz, "Unknown properties: " + unknownNames);
		}
	}
}
