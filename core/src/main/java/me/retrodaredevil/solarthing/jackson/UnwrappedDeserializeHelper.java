package me.retrodaredevil.solarthing.jackson;

import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helps you deserialize something by using a lenient mapper and throwing an exception if there
 * were unused properties.
 *
 * NOTE: This only works under the assumption that the values you read can be serialized and deserialized the same way.
 */
@NullMarked
public class UnwrappedDeserializeHelper {
	private final ObjectMapper mapper;
	private final ObjectReader reader;
	private final JsonParser parser;
	private final Class<?> clazz;
	private final ObjectNode objectNode;

	public UnwrappedDeserializeHelper(JsonParser parser, DeserializationContext context, Class<?> clazz) {
		this.parser = parser;
		this.clazz = clazz;
		this.objectNode = parser.readValueAsTree();
		mapper = JacksonUtil.defaultMapper();
		reader = mapper.reader().with(context.getConfig()).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	public <T> T readValue(Class<T> clazz) {
		T r = reader.treeToValue(objectNode, clazz);
		for (Iterator<String> it = mapper.valueToTree(r).propertyNames().iterator(); it.hasNext(); ){
			objectNode.remove(it.next());
		}
		return r;
	}
	public void assertNoUnknown() {
		List<String> unknownNames = new ArrayList<>();
		for (Iterator<String> it = objectNode.propertyNames().iterator(); it.hasNext(); ){
			String unknownPropertyName = it.next();
			unknownNames.add(unknownPropertyName);
		}
		if (!unknownNames.isEmpty()) {
			throw MismatchedInputException.from(parser, clazz, "Unknown properties: " + unknownNames);
		}
	}
}
