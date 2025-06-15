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
import me.retrodaredevil.solarthing.actions.CommonActionUtil;
import me.retrodaredevil.solarthing.actions.config.ActionFormat;
import me.retrodaredevil.solarthing.actions.config.ActionReference;
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
import me.retrodaredevil.solarthing.config.databases.DatabaseConfig;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class DeserializeTest {
	private static final Path SOLARTHING_ROOT = Path.of(".."); // Working directory for tests are the /client folder
	private static final Path BASE_CONFIG_DIRECTORY = SOLARTHING_ROOT.resolve("config_templates/base");
	private static final Path ACTION_JSON_CONFIG_DIRECTORY = SOLARTHING_ROOT.resolve("config_templates/actions");
	private static final Path ACTION_NS_CONFIG_DIRECTORY = SOLARTHING_ROOT.resolve("config_templates/actions-ns");
	private static final Path DATABASE_CONFIG_DIRECTORY = SOLARTHING_ROOT.resolve("config_templates/databases");
	private static final Path IO_CONFIG_DIRECTORY = SOLARTHING_ROOT.resolve("config_templates/io");

	private static final DirectoryStream.Filter<? super Path> JSON_FILTER = path -> path.getFileName().toString().endsWith(".json");
	private static final DirectoryStream.Filter<? super Path> NOTATION_SCRIPT_FILTER = path -> path.getFileName().toString().endsWith(".ns");

	private static final ObjectMapper MAPPER = ActionUtil.registerActionNodes(DatabaseSettingsUtil.registerDatabaseSettings(JacksonUtil.defaultMapper()));

	@Test
	void testInfluxDb1() throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String json = """
				{
				  "type": "influxdb",
				  "config": {
				    "url": "http://localhost:8086",
				    "username": "root",
				    "password": "root",
				    "database": "default_database",
				    "measurement": null,

				    "status_retention_policies": [
				      {
				        "frequency": 120,
				        "name": "autogen"
				      }
				    ],

				    "event_retention_policy": {
				      "name": "autogen"
				    }
				  }
				}""";
		mapper.registerSubtypes(DatabaseSettings.class, InfluxDbDatabaseSettings.class);
		DatabaseConfig config = mapper.readValue(json, DatabaseConfig.class);
		assertInstanceOf(InfluxDbDatabaseSettings.class, config.requireDatabaseSettings());
	}

	private List<Path> getJsonFiles(Path directory) throws IOException {
		List<Path> files = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, JSON_FILTER)) {
			directoryStream.forEach(files::add);
		}
		assertTrue(files.size() >= 1);
		return files;
	}
	private List<Path> getNotationScriptFiles(Path directory) throws IOException {
		List<Path> files = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, NOTATION_SCRIPT_FILTER)) {
			directoryStream.forEach(files::add);
		}
		assertTrue(files.size() >= 1);
		return files;
	}

	@Test
	void testAllBaseConfigs() throws IOException {
		for (Path configFile : getJsonFiles(BASE_CONFIG_DIRECTORY)) {
			try {
				CommonConfigUtil.readConfig(configFile, ProgramOptions.class, MAPPER);
			} catch (ConfigException ex) {
				fail("Failed parsing config: " + configFile, ex);
			}
		}
	}
	@Test
	void testAllDatabases() throws IOException {
		for (Path configFile : getJsonFiles(DATABASE_CONFIG_DIRECTORY)) {
			try {
				CommonConfigUtil.readConfig(configFile, DatabaseConfig.class, MAPPER);
			} catch (ConfigException ex) {
				fail("Failed parsing config: " + configFile, ex);
			}
		}
	}
	@Test
	void testJsonActions() throws IOException {
		/*
		Also note that mattermost stuff was here, so we may not need it after it was removed on 2021.05.13

		ObjectMapper mapper = MAPPER.copy();
		InjectableValues.Std iv = new InjectableValues.Std();
		Map<String, File> hardcodedFileMap = new HashMap<>();
		iv.addValue(FileMapper.JACKSON_INJECT_IDENTIFIER, (FileMapper) hardcodedFileMap::get);
		mapper.setInjectableValues(iv);
		TODO in future, uncomment if https://github.com/FasterXML/jackson-databind/issues/3072 is implemented
		 */

		for (Path configFile : getJsonFiles(ACTION_JSON_CONFIG_DIRECTORY)) {
			if (configFile.endsWith("message_sender.json")) {
				// We cannot test this one because it tries to read the file "config/slack.json", and we currently don't have a mechanism to change that
				continue;
			}
			try {
				CommonConfigUtil.readConfig(configFile, ActionNode.class, MAPPER);
			} catch (ConfigException ex) {
				fail("Failed parsing config: " + configFile, ex);
			}
		}
	}
	@Test
	void testNotationScriptActions() throws IOException {
		for (Path notationScriptFile : getNotationScriptFiles(ACTION_NS_CONFIG_DIRECTORY)) {
			ActionReference actionReference = new ActionReference(notationScriptFile, ActionFormat.NOTATION_SCRIPT);
			try {
				CommonActionUtil.readActionReference(MAPPER, actionReference);
			} catch (IOException ex) {
				fail("Failed parsing script: " + notationScriptFile, ex);
			}
		}
	}
	@Test
	void testAllIO() throws IOException {
		ObjectMapper mapper = MAPPER.copy();
		InjectableValues.Std iv = new InjectableValues.Std();
		iv.addValue(SerialIOConfig.DEFAULT_SERIAL_CONFIG_KEY, new SerialConfigBuilder(9600));
		mapper.setInjectableValues(iv);

		for (Path configFile : getJsonFiles(IO_CONFIG_DIRECTORY)) {
			try {
				CommonConfigUtil.readConfig(configFile, IOConfig.class, mapper);
			} catch (ConfigException ex) {
				fail("Failed parsing config: " + configFile, ex);
			}
		}
		{
			// many users of SolarThing have come to rely on config_templates/io/default_linux_serial.json. Let's make sure it's always there for them
			Path file = IO_CONFIG_DIRECTORY.resolve("default_linux_serial.json");
			CommonConfigUtil.readConfig(file, IOConfig.class, mapper);
		}
	}

	@Test
	void testRunAlertGeneratorOffWhileAuxOn() throws IOException, ParsePacketAsciiDecimalDigitException, CheckSumException {
		Path file = ACTION_JSON_CONFIG_DIRECTORY.resolve("alert_generator_off_while_aux_on.json");
		ActionNode jsonActionNode = CommonConfigUtil.readConfig(file, ActionNode.class, MAPPER);

		Path notationScriptPath = ACTION_NS_CONFIG_DIRECTORY.resolve("alert_generator_off_while_aux_on.ns");
		ActionReference actionReference = new ActionReference(notationScriptPath, ActionFormat.NOTATION_SCRIPT);
		ActionNode nsActionNode = CommonActionUtil.readActionReference(MAPPER, actionReference);

		testRunAlertGeneratorOffWhileAuxOn(jsonActionNode);
		testRunAlertGeneratorOffWhileAuxOn(nsActionNode);
	}

	void testRunAlertGeneratorOffWhileAuxOn(ActionNode actionNode) throws ParsePacketAsciiDecimalDigitException, CheckSumException {
		// We need to simulate an automation program environment to run this action
		Duration[] timeReference = new Duration [] { Duration.ZERO };
		FragmentedPacketGroup[] packetGroupReference = new FragmentedPacketGroup[] { null };
		final int FRAGMENT_ID = 1; // the fragment ID used in the required action in the alert ActionLang script

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
			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(packet), 0L, "my_source_id", FRAGMENT_ID);
			Action action = actionNode.createAction(new ActionEnvironment(new VariableEnvironment(), injectEnvironment));
			action.update();
			assertTrue(action.isDone());
		}

		{ // Test that no alert is sent unless the aux is on, and it's in No AC for 30 seconds
			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(auxOnNoAC), 0L, "my_source_id", FRAGMENT_ID);
			Action action = actionNode.createAction(new ActionEnvironment(new VariableEnvironment(), injectEnvironment));
			action.update();
			assertFalse(action.isDone());
			timeReference[0] = timeReference[0].plus(Duration.ofSeconds(44));
			action.update();
			assertFalse(action.isDone());

			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(auxOnACUse), 0L, "my_source_id", FRAGMENT_ID);
			action.update();
			assertTrue(action.isDone()); // No alert has been sent, since it started to AC Use before the 30 second period completed.
		}
		{ // Test that the alert gets sent and the action doesn't end until the 300-second timeout completes
			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(auxOnNoAC), 0L, "my_source_id", FRAGMENT_ID);
			Action action = actionNode.createAction(new ActionEnvironment(new VariableEnvironment(), injectEnvironment));
			action.update();
			assertFalse(action.isDone());
			timeReference[0] = timeReference[0].plus(Duration.ofSeconds(45));
			action.update();
			assertFalse(action.isDone());

			packetGroupReference[0] = PacketGroups.createInstancePacketGroup(Collections.singleton(auxOnACUse), 0L, "my_source_id", FRAGMENT_ID);
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
