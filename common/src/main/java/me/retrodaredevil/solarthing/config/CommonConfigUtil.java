package me.retrodaredevil.solarthing.config;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.apache.commons.text.StringSubstitutor;
import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
@NullMarked
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

	private static final ObjectMapper VANILLA_MAPPER = JsonMapper.builder().build();

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
			node.propertyStream().forEach(entry -> {
				String newKey = INTERPOLATOR.replace(entry.getKey());
				JsonNode newValue = interpolate(entry.getValue());
				objectNode.set(newKey, newValue);
			});
			return objectNode;
		} else if (node.isString()) {
			return new StringNode(INTERPOLATOR.replace(node.asString()));
		}
		return node;
	}

	public static JsonNode readAndInterpolate(Path file) {
		final JsonNode node;
		try (InputStream inputStream = Files.newInputStream(file)) {
			try {
				node = VANILLA_MAPPER.readTree(inputStream);
			} catch (JacksonException e) {
				throw createExceptionFromJackson(file.toString(), e);
			}
		} catch (IOException e) {
			throw new ConfigException("Exception while reading: " + file, e);
		}
		return interpolate(node);
	}

	/**
	 * @param fileRepresentation The string representing the file.
	 * @param jacksonException The exception from a Jackson method
	 * @return The resulting config exception with a customized message
	 */
	public static ConfigException createExceptionFromJackson(String fileRepresentation, JacksonException jacksonException) {
		String exceptionMessage = jacksonException.getMessage();
		if (exceptionMessage != null && exceptionMessage.contains("end-of-input")) {
			throw new ConfigException("Invalid JSON in file: " + fileRepresentation + " (check formatting)", jacksonException);
		}
		if (jacksonException instanceof DatabindException) {
			if (jacksonException instanceof UnrecognizedPropertyException) {
				String internalMessage = jacksonException.getMessage();
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
				throw new ConfigException("Unrecognized config property in file: " + fileRepresentation + " Please make sure you don't have any typos." + extraMessage, jacksonException);
			}
			throw new ConfigException("Couldn't parse data from file: " + fileRepresentation + " Please make sure all your properties are correct", jacksonException);
		}
		throw new ConfigException("Couldn't parse data from file: " + fileRepresentation + " Please make sure your JSON is formatted correctly.", jacksonException);
	}

	public static <T> T readConfig(Path file, Class<T> clazz, ObjectMapper objectMapper) {
		JsonNode node = readAndInterpolate(file);
		try {
			return objectMapper.treeToValue(node, clazz);
		} catch (JacksonException e) {
			throw createExceptionFromJackson(file.toString(), e);
		}
	}
}
