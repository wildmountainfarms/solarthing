package me.retrodaredevil.solarthing.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.program.DatabaseConfig;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeserializeTest {
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
}
