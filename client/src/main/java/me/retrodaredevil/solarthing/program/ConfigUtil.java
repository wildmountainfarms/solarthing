package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettingsUtil;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.io.SerialIOConfig;
import me.retrodaredevil.solarthing.config.options.DatabaseOption;
import me.retrodaredevil.solarthing.config.options.PacketHandlingOption;
import me.retrodaredevil.solarthing.exceptions.ConfigException;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class ConfigUtil {
	private ConfigUtil() { throw new UnsupportedOperationException(); }

	// https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringSubstitutor.html#using-interpolation
	/**
	 * The interpolator to convert strings potentially containing variables or references to a string with the evaluated expressions.
	 * This works most similar to <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html#property-substitution">log4j/2.x/manual/configuration.html#property-substitution</a>
	 * <p>
	 * Documentation here: <a href="https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringSubstitutor.html">StringSubstitutor</a>
	 * then scroll to "Using Interpolation".
	 */
	public static final StringSubstitutor INTERPOLATOR = StringSubstitutor.createInterpolator();
	static final ObjectMapper MAPPER = DatabaseSettingsUtil.registerDatabaseSettings(JacksonUtil.defaultMapper());
	/** A mapper to use only if you are using it to parse JSON and are not using it for deserialization. */
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

	private static JsonNode readAndInterpolate(Path file) {
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
	private static ConfigException createExceptionFromJackson(String fileRepresentation, IOException jacksonIOException) {
		if (jacksonIOException.getMessage().contains("end-of-input")) {
			throw new ConfigException("Invalid JSON in file: " + fileRepresentation + " (check formatting)", jacksonIOException);
		}
		throw new ConfigException("Couldn't parse data from file: " + fileRepresentation + " Please make sure your JSON is correct.", jacksonIOException);
	}

	public static List<DatabaseConfig> getDatabaseConfigs(PacketHandlingOption options){
		List<Path> files = options.getDatabaseConfigurationFilePaths();
		List<DatabaseConfig> r = new ArrayList<>();
		for(Path file : files){
			r.add(getDatabaseConfig(file));
		}
		return r;
	}
	public static DatabaseConfig getDatabaseConfig(Path file){
		return readConfig(file, DatabaseConfig.class);
	}
	public static <T> T readConfig(Path file, Class<T> clazz) {
		return readConfig(file, clazz, MAPPER);
	}
	public static <T> T readConfig(Path file, Class<T> clazz, ObjectMapper objectMapper) {
		JsonNode node = readAndInterpolate(file);
		try {
			return objectMapper.treeToValue(node, clazz);
		} catch (IOException e) {
			throw createExceptionFromJackson(file.toString(), e);
		}
	}

	public static IOConfig parseIOConfig(Path file, SerialConfig defaultSerialConfig) {
		JsonNode node = readAndInterpolate(file);

		InjectableValues.Std iv = new InjectableValues.Std();
		iv.addValue(SerialIOConfig.DEFAULT_SERIAL_CONFIG_KEY, defaultSerialConfig);

		ObjectMapper mapper = JacksonUtil.configurationMapper(JacksonUtil.defaultMapper());
		mapper.setInjectableValues(iv);
		final IOConfig config;
		try {
			config = mapper.treeToValue(node, IOConfig.class);
		} catch (IOException e) {
			throw createExceptionFromJackson(file.toString(), e);
		}
		return config;
	}
	public static IOBundle createIOBundle(Path file, SerialConfig defaultSerialConfig) {
		IOConfig config = parseIOConfig(file, defaultSerialConfig);
		try {
			return config.createIOBundle();
		} catch (Exception e) {
			final String extra;
			if (config instanceof SerialIOConfig) {
				extra = " Make sure that the serial port is correct and you have the right permissions.";
			} else {
				extra = "";
			}
			// Note: If we consider using exit codes in the future to determine whether the program should restart,
			//   and if we decide that ConfigExceptions should warrant a restart to not occur, then we may want to reconsider
			//   this particular case because it's possible for the serial port to be connected in the future.
			// Or, it might not matter because this particular method isn't used very often, as you cannot use this method
			//   if you need a ReloadableIOBundle
			throw new ConfigException(
					"Could not create IO bundle from config file: " + file,
					e,
					"Your IO configuration was successfully parsed from " + file + ", but we failed to initialize it." + extra
			);
		}
	}
	public static CouchDbDatabaseSettings expectCouchDbDatabaseSettings(DatabaseOption options) {
		DatabaseConfig databaseConfig = getDatabaseConfig(options.getDatabaseFilePath());
		DatabaseType databaseType = databaseConfig.getType();
		if(databaseType != CouchDbDatabaseSettings.TYPE){
			throw new IllegalArgumentException("Only CouchDb can be used for this program type right now!");
		}
		return (CouchDbDatabaseSettings) databaseConfig.getSettings();
	}
}
