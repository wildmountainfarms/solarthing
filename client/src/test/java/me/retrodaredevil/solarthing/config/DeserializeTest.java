package me.retrodaredevil.solarthing.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettingsUtil;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.io.SerialIOConfig;
import me.retrodaredevil.solarthing.config.options.ProgramOptions;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.program.ActionUtil;
import me.retrodaredevil.solarthing.program.DatabaseConfig;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class DeserializeTest {
	private static final File SOLARTHING_ROOT = new File(".."); // Working directory for tests are the /client folder
	private static final File BASE_CONFIG_DIRECTORY = new File(SOLARTHING_ROOT, "config_templates/base");
	private static final File ACTION_CONFIG_DIRECTORY = new File(SOLARTHING_ROOT, "config_templates/actions");
	private static final File DATABASE_CONFIG_DIRECTORY = new File(SOLARTHING_ROOT, "config_templates/databases");
	private static final File IO_CONFIG_DIRECTORY = new File(SOLARTHING_ROOT, "config_templates/io");

	private static final FileFilter JSON_FILTER = file -> file.getName().endsWith(".json");

	private static final ObjectMapper MAPPER = ActionUtil.registerActionNodes(DatabaseSettingsUtil.registerDatabaseSettings(JacksonUtil.defaultMapper()));

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
		assertTrue(files.length >= 3);
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
		Also note that mattermost stuff was here, so we may not need it after it was removed on 2021.05.13

		ObjectMapper mapper = MAPPER.copy();
		InjectableValues.Std iv = new InjectableValues.Std();
		Map<String, File> hardcodedFileMap = new HashMap<>();
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
	@Test
	void testAllIO() {
		ObjectMapper mapper = MAPPER.copy();
		InjectableValues.Std iv = new InjectableValues.Std();
		iv.addValue(SerialIOConfig.DEFAULT_SERIAL_CONFIG_KEY, new SerialConfigBuilder(9600));
		mapper.setInjectableValues(iv);

		for (File configFile : getJsonFiles(IO_CONFIG_DIRECTORY)) {
			try {
				mapper.readValue(configFile, IOConfig.class);
			} catch (IOException ex) {
				fail("Failed parsing config: " + configFile, ex);
			}
		}
		{
			// many users of SolarThing have come to rely on config_templates/io/default_linux_serial.json. Let's make sure it's always there for them
			File file = new File(IO_CONFIG_DIRECTORY, "default_linux_serial.json");
			assertTrue(file.exists(), "default_linux_serial.json doesn't exist!");
			try {
				mapper.readValue(file, IOConfig.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Test
	void testRunAlertGeneratorOffWhileAuxOn() throws IOException, ParsePacketAsciiDecimalDigitException, CheckSumException {
		File file = new File(ACTION_CONFIG_DIRECTORY, "alert_generator_off_while_aux_on.json");
		ActionNode actionNode = MAPPER.readValue(file, ActionNode.class);
		// We need to simulate an automation program environment to run this action
		Duration[] timeReference = new Duration [] { Duration.ZERO };
		FragmentedPacketGroup[] packetGroupReference = new FragmentedPacketGroup[] { null };

		FragmentedPacketGroupProvider fragmentedPacketGroupProvider = () -> requireNonNull(packetGroupReference[0]);

		InjectEnvironment injectEnvironment = new InjectEnvironment.Builder()
				.add(new NanoTimeProviderEnvironment(() -> timeReference[0].toNanos()))
				.add(new LatestPacketGroupEnvironment(fragmentedPacketGroupProvider))
				.add(new LatestFragmentedPacketGroupEnvironment(fragmentedPacketGroupProvider))
				.build();
		FXStatusPacket auxOnNoAC = FXStatusPackets.createFromChars("\n1,00,00,02,123,123,00,10,000,00,252,136,000,999\r".toCharArray(), IgnoreCheckSum.IGNORE);
		FXStatusPacket auxOffNoAC = FXStatusPackets.createFromChars("\n1,00,00,02,123,123,00,10,000,00,252,008,000,999\r".toCharArray(), IgnoreCheckSum.IGNORE);
		FXStatusPacket auxOnACUse = FXStatusPackets.createFromChars("\n1,00,00,02,123,123,00,10,000,02,252,136,000,999\r".toCharArray(), IgnoreCheckSum.IGNORE);
		FXStatusPacket auxOffACUse = FXStatusPackets.createFromChars("\n1,00,00,02,123,123,00,10,000,02,252,008,000,999\r".toCharArray(), IgnoreCheckSum.IGNORE);

		for (FXStatusPacket packet : new FXStatusPacket[] { auxOffNoAC, auxOnACUse, auxOffACUse }) {
			// for these three cases, the action should end immediately
			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(packet), 0L, "my_source_id", 999);
			Action action = actionNode.createAction(new ActionEnvironment(new VariableEnvironment(), injectEnvironment));
			action.update();
			assertTrue(action.isDone());
		}

		{ // Test that no alert is sent unless the aux is on, and it's in No AC for 30 seconds
			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(auxOnNoAC), 0L, "my_source_id", 999);
			Action action = actionNode.createAction(new ActionEnvironment(new VariableEnvironment(), injectEnvironment));
			action.update();
			assertFalse(action.isDone());
			timeReference[0] = timeReference[0].plus(Duration.ofSeconds(44));
			action.update();
			assertFalse(action.isDone());

			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(auxOnACUse), 0L, "my_source_id", 999);
			action.update();
			assertTrue(action.isDone()); // No alert has been sent, since it started to AC Use before the 30 second period completed.
		}
		{ // Test that the alert gets sent and the action doesn't end until the 300-second timeout completes
			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(auxOnNoAC), 0L, "my_source_id", 999);
			Action action = actionNode.createAction(new ActionEnvironment(new VariableEnvironment(), injectEnvironment));
			action.update();
			assertFalse(action.isDone());
			timeReference[0] = timeReference[0].plus(Duration.ofSeconds(45));
			action.update();
			assertFalse(action.isDone());

			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(auxOnACUse), 0L, "my_source_id", 999);
			action.update();
			assertFalse(action.isDone()); // Alert has been sent, so the action isn't going to end

			timeReference[0] = timeReference[0].plus(Duration.ofSeconds(299));
			action.update();
			assertFalse(action.isDone());

			timeReference[0] = timeReference[0].plus(Duration.ofSeconds(1));
			action.update();
			assertTrue(action.isDone()); // the 300-second timeout has completed, so the action will end
		}
	}
}
