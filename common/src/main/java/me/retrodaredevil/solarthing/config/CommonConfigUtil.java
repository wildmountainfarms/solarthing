package me.retrodaredevil.solarthing.config;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

@UtilityClass
public class CommonConfigUtil {
	private CommonConfigUtil() { throw new UnsupportedOperationException(); }

	// https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringSubstitutor.html#using-interpolation
	/**
	 * The interpolator to convert strings potentially containing variables or references to a string with the evaluated expressions.
	 * This works most similar to <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html#property-substitution">log4j/2.x/manual/configuration.html#property-substitution</a>
	 * <p>
	 * Documentation here: <a href="https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringSubstitutor.html">StringSubstitutor</a>
	 * then scroll to "Using Interpolation".
	 */
	static final StringSubstitutor INTERPOLATOR = StringSubstitutor.createInterpolator();

	private static final ObjectMapper VANILLA_MAPPER = new ObjectMapper();

	static {
		INTERPOLATOR.setEnableSubstitutionInVariables(true); // Allows for nested $'s.
	}

	static JsonNode interpolate(JsonNode node) {
		if (node.isArray()) {
			ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
			for (JsonNode innerNode : node) {
				arrayNode.add(interpolate(innerNode));
			}
			return arrayNode;
		} else if (node.isObject()) {
			ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
			for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
				Map.Entry<String, JsonNode> entry = it.next();
				String newKey = INTERPOLATOR.replace(entry.getKey());
				JsonNode newValue = interpolate(entry.getValue());
				objectNode.set(newKey, newValue);
			}
			return objectNode;
		} else if (node.isTextual()) {
			return new TextNode(INTERPOLATOR.replace(node.asText()));
		}
		return node;
	}

	public static JsonNode readAndInterpolate(Path file) {
		final JsonNode node;
		try (InputStream inputStream = Files.newInputStream(file)) {
			try {
				node = VANILLA_MAPPER.readTree(inputStream);
			} catch (IOException e) {
				throw createExceptionFromJackson(file.toString(), e);
			}
		} catch (IOException e) {
			throw new ConfigException("Exception while reading: " + file, e);
		}
		return interpolate(node);
	}

	/**
	 * @param fileRepresentation The string representing the file.
	 * @param jacksonIOException The IOException occurring from a calling a Jackson method
	 * @return The resulting config exception with a customized message
	 */
	public static ConfigException createExceptionFromJackson(String fileRepresentation, IOException jacksonIOException) {
		if (jacksonIOException.getMessage().contains("end-of-input")) {
			throw new ConfigException("Invalid JSON in file: " + fileRepresentation + " (check formatting)", jacksonIOException);
		}
		if (jacksonIOException instanceof DatabindException) {
			if (jacksonIOException instanceof UnrecognizedPropertyException) {
				String internalMessage = jacksonIOException.getMessage();
				final String property;
				if (internalMessage != null && internalMessage.startsWith("Unrecognized field \"")) {
					String[] splitMessage = internalMessage.split("\"", 3);
					if (splitMessage.length == 3) {
						property = splitMessage[1];
					} else {
						property = null;
					}
				} else {
					property = null;
				}
				String extraMessage = property != null ? " Unrecognized property: " + property : "";
				throw new ConfigException("Unrecognized config property in file: " + fileRepresentation + " Please make sure you don't have any typos." + extraMessage, jacksonIOException);
			}
			throw new ConfigException("Couldn't parse data from file: " + fileRepresentation + " Please make sure all your properties are correct", jacksonIOException);
		}
		throw new ConfigException("Couldn't parse data from file: " + fileRepresentation + " Please make sure your JSON is formatted correctly.", jacksonIOException);
	}

	public static <T> T readConfig(Path file, Class<T> clazz, ObjectMapper objectMapper) {
		JsonNode node = readAndInterpolate(file);
		try {
			return objectMapper.treeToValue(node, clazz);
		} catch (IOException e) {
			throw createExceptionFromJackson(file.toString(), e);
		}
	}
}
