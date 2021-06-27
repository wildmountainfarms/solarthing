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
			throw new RuntimeException(e);
		}
		try {
			return MAPPER.readValue(reader, DatabaseConfig.class);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't parse data!", e);
		}
	}

	public static IOConfig parseIOConfig(File configFile, SerialConfig defaultSerialConfig) {
		final FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(configFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		InjectableValues.Std iv = new InjectableValues.Std();
		iv.addValue(SerialIOConfig.DEFAULT_SERIAL_CONFIG_KEY, defaultSerialConfig);

		ObjectMapper mapper = JacksonUtil.configurationMapper(JacksonUtil.defaultMapper());
		mapper.setInjectableValues(iv);
		final IOConfig config;
		try {
			config = mapper.readValue(inputStream, IOConfig.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return config;
	}
	public static IOBundle createIOBundle(File configFile, SerialConfig defaultSerialConfig) {
		IOConfig config = parseIOConfig(configFile, defaultSerialConfig);
		try {
			return config.createIOBundle();
		} catch (Exception e) {
			throw new RuntimeException(e);
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
