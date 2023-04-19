package me.retrodaredevil.solarthing.config;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettingsUtil;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.io.SerialIOConfig;
import me.retrodaredevil.solarthing.config.options.DatabaseConfigSettings;
import me.retrodaredevil.solarthing.config.options.DatabaseOption;
import me.retrodaredevil.solarthing.config.options.PacketHandlingOption;
import me.retrodaredevil.solarthing.config.databases.DatabaseConfig;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class ConfigUtil {
	private ConfigUtil() { throw new UnsupportedOperationException(); }

	static final ObjectMapper MAPPER = DatabaseSettingsUtil.registerDatabaseSettings(JacksonUtil.defaultMapper());
	/** A mapper to use only if you are using it to parse JSON and are not using it for deserialization. */

	public static List<DatabaseConfig> resolveConfigs(DatabaseConfigSettings databaseConfigSettings) {
		return databaseConfigSettings.resolveConfigs(MAPPER);
	}

	@Deprecated
	public static List<DatabaseConfig> getDatabaseConfigs(PacketHandlingOption options){
		List<Path> files = options.getDatabaseConfigurationFilePaths();
		List<DatabaseConfig> r = new ArrayList<>();
		for(Path file : files){
			DatabaseConfig config = readDatabaseConfig(file)
					.resolveExternal(MAPPER);
			r.add(config);
		}
		return r;
	}
	public static DatabaseConfig readDatabaseConfig(Path file){
		return readConfig(file, DatabaseConfig.class);
	}
	public static <T> T readConfig(Path file, Class<T> clazz) {
		return CommonConfigUtil.readConfig(file, clazz, MAPPER);
	}

	public static IOConfig parseIOConfig(Path file, SerialConfig defaultSerialConfig) {
		JsonNode node = CommonConfigUtil.readAndInterpolate(file);

		InjectableValues.Std iv = new InjectableValues.Std();
		iv.addValue(SerialIOConfig.DEFAULT_SERIAL_CONFIG_KEY, defaultSerialConfig);

		ObjectMapper mapper = JacksonUtil.configurationMapper(JacksonUtil.defaultMapper());
		mapper.setInjectableValues(iv);
		final IOConfig config;
		try {
			config = mapper.treeToValue(node, IOConfig.class);
		} catch (IOException e) {
			throw CommonConfigUtil.createExceptionFromJackson(file.toString(), e);
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
		DatabaseConfig databaseConfig = readDatabaseConfig(options.getDatabaseFilePath());
		DatabaseType databaseType = databaseConfig.getType();
		if(databaseType != CouchDbDatabaseSettings.TYPE){
			throw new ConfigException("Only CouchDb can be used for this program type right now!");
		}
		return (CouchDbDatabaseSettings) databaseConfig.requireDatabaseSettings();
	}
}
