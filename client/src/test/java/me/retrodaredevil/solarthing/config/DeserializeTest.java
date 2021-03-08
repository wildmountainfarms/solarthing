package me.retrodaredevil.solarthing.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettingsUtil;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.io.SerialIOConfig;
import me.retrodaredevil.solarthing.config.options.ProgramOptions;
import me.retrodaredevil.solarthing.program.DatabaseConfig;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DeserializeTest {
	private static final File SOLARTHING_ROOT = new File(".."); // Working directory for tests are the /client folder
	private static final File BASE_CONFIG_DIRECTORY = new File(SOLARTHING_ROOT, "config_templates/base");
	private static final File ACTION_CONFIG_DIRECTORY = new File(SOLARTHING_ROOT, "config_templates/actions");
	private static final File DATABASE_CONFIG_DIRECTORY = new File(SOLARTHING_ROOT, "config_templates/databases");

	private static final FileFilter JSON_FILTER = new SuffixFileFilter(".json");

	private static final ObjectMapper MAPPER = DatabaseSettingsUtil.registerDatabaseSettings(JacksonUtil.defaultMapper());

	@Test
	void testInfluxDb1() throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String json = "{\n" +
				"  \"type\": \"influxdb\",\n" +
				"  \"config\": {\n" +
				"    \"url\": \"http://localhost:8086\",\n" +
				"    \"username\": \"root\",\n" +
				"    \"password\": \"root\",\n" +
				"    \"database\": \"default_database\",\n" +
				"    \"measurement\": null,\n" +
				"\n" +
				"    \"status_retention_policies\": [\n" +
				"      {\n" +
				"        \"frequency\": 120,\n" +
				"        \"name\": \"autogen\"\n" +
				"      }\n" +
				"    ],\n" +
				"\n" +
				"    \"event_retention_policy\": {\n" +
				"      \"name\": \"autogen\"\n" +
				"    }\n" +
				"  }\n" +
				"}";
		mapper.registerSubtypes(DatabaseSettings.class, InfluxDbDatabaseSettings.class);
		DatabaseConfig config = mapper.readValue(json, DatabaseConfig.class);
		assertTrue(config.getSettings() instanceof InfluxDbDatabaseSettings);
	}

	private File[] getJsonFiles(File directory) {
		assertTrue(directory.isDirectory(), "Not directory! " + directory + " absolute: " + directory.getAbsolutePath());
		File[] files = directory.listFiles(JSON_FILTER);
		assertTrue(files.length > 5);
		return files;
	}

	@Test
	void testAllBaseConfigs() {
		for (File configFile : getJsonFiles(BASE_CONFIG_DIRECTORY)) {
			try {
				MAPPER.readValue(configFile, ProgramOptions.class);
			} catch (IOException ex) {
				fail("Failed parsing config: " + configFile, ex);
			}
		}
	}
	@Test
	void testAllDatabases() {
		for (File configFile : getJsonFiles(DATABASE_CONFIG_DIRECTORY)) {
			try {
				MAPPER.readValue(configFile, DatabaseConfig.class);
			} catch (IOException ex) {
				fail("Failed parsing config: " + configFile, ex);
			}
		}
	}
	@Test
	void testAllActions() {
		/*
		ObjectMapper mapper = MAPPER.copy();
		InjectableValues.Std iv = new InjectableValues.Std();
		Map<String, File> hardcodedFileMap = new HashMap<>();
		hardcodedFileMap.put("config/mattermost_important.json", new File(SOLARTHING_ROOT, "config_templates/message/mattermost_template.json"));
		hardcodedFileMap.put("config/mattermost.json", new File(SOLARTHING_ROOT, "config_templates/message/mattermost_template.json"));
		iv.addValue(FileMapper.JACKSON_INJECT_IDENTIFIER, (FileMapper) hardcodedFileMap::get);
		mapper.setInjectableValues(iv);
		TODO in future, uncomment if https://github.com/FasterXML/jackson-databind/issues/3072 is implemented
		 */

		for (File configFile : getJsonFiles(ACTION_CONFIG_DIRECTORY)) {
			if (configFile.getName().equals("message_sender.json")) {
				// We cannot test this one because it tries to read the file "config/mattermost.json", and we currently don't have a mechanism to change that
				continue;
			}
			try {
				MAPPER.readValue(configFile, ActionNode.class);
			} catch (IOException ex) {
				fail("Failed parsing config: " + configFile, ex);
			}
		}
	}
}
