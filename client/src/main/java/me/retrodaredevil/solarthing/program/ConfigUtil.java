package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class ConfigUtil {
	private ConfigUtil() { throw new UnsupportedOperationException(); }

	static final ObjectMapper MAPPER = DatabaseSettingsUtil.registerDatabaseSettings(JacksonUtil.defaultMapper());

	private static ConfigException createExceptionFromJackson(File file, IOException jacksonIOException) {
		if (jacksonIOException.getMessage().contains("end-of-input")) {
			throw new ConfigException("Invalid JSON in file: " + file + " (check formatting)", jacksonIOException);
		}
		throw new ConfigException("Couldn't parse data from file: " + file + " Please make sure your JSON is correct.", jacksonIOException);
	}

	public static List<DatabaseConfig> getDatabaseConfigs(PacketHandlingOption options){
		List<File> files = options.getDatabaseConfigurationFiles();
		List<DatabaseConfig> r = new ArrayList<>();
		for(File file : files){
			r.add(getDatabaseConfig(file));
		}
		return r;
	}
	public static DatabaseConfig getDatabaseConfig(File file){
		FileInputStream reader;
		try {
			reader = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new ConfigException("Could not find the file: " + file, e);
		}
		try {
			return MAPPER.readValue(reader, DatabaseConfig.class);
		} catch (IOException e) {
			throw createExceptionFromJackson(file, e);
		}
	}

	public static IOConfig parseIOConfig(File configFile, SerialConfig defaultSerialConfig) {
		final FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(configFile);
		} catch (FileNotFoundException e) {
			throw new ConfigException("Could not find file: " + configFile, e);
		}
		InjectableValues.Std iv = new InjectableValues.Std();
		iv.addValue(SerialIOConfig.DEFAULT_SERIAL_CONFIG_KEY, defaultSerialConfig);

		ObjectMapper mapper = JacksonUtil.configurationMapper(JacksonUtil.defaultMapper());
		mapper.setInjectableValues(iv);
		final IOConfig config;
		try {
			config = mapper.readValue(inputStream, IOConfig.class);
		} catch (IOException e) {
			throw createExceptionFromJackson(configFile, e);
		}
		return config;
	}
	public static IOBundle createIOBundle(File configFile, SerialConfig defaultSerialConfig) {
		IOConfig config = parseIOConfig(configFile, defaultSerialConfig);
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
					"Could not create IO bundle from config file: " + configFile,
					e,
					"Your IO configuration was successfully parsed from " + configFile + ", but we failed to initialize it." + extra
			);
		}
	}
	public static CouchDbDatabaseSettings expectCouchDbDatabaseSettings(DatabaseOption options) {
		DatabaseConfig databaseConfig = getDatabaseConfig(options.getDatabase());
		DatabaseType databaseType = databaseConfig.getType();
		if(databaseType != CouchDbDatabaseSettings.TYPE){
			throw new IllegalArgumentException("Only CouchDb can be used for this program type right now!");
		}
		return (CouchDbDatabaseSettings) databaseConfig.getSettings();
	}
}
