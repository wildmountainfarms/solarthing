package me.retrodaredevil.solarthing.rest.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Collections;

@Component
public class CommandHandlerProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlerProvider.class);
	private static final ObjectMapper SIMPLE_OBJECT_MAPPER = JacksonUtil.defaultMapper();

	@Value("${solarthing.config.command_file:#{null}}")
	private @Nullable Path commandConfigurationFile;

	private final SolarThingDatabase solarThingDatabase;


	private CommandHandler commandHandler;

	public CommandHandlerProvider(CouchDbDatabaseSettings couchDbDatabaseSettings) {
		this.solarThingDatabase = CouchDbSolarThingDatabase.create(CouchDbUtil.createInstance(
				couchDbDatabaseSettings.getCouchProperties(),
				couchDbDatabaseSettings.getOkHttpProperties()
		));
	}

	@PostConstruct
	public void init() {
		final RestCommandConfig config;
		if (commandConfigurationFile == null) {
			config = new RestCommandConfig(Collections.emptyList(), Collections.emptyMap());
			LOGGER.debug("No command configuration file. No one will be authorized to send commands.");
		} else {
			try (InputStream inputStream = Files.newInputStream(commandConfigurationFile)) {
				// TODO [Interpolate] allow interpolated values in config file
				config = SIMPLE_OBJECT_MAPPER.readValue(inputStream, RestCommandConfig.class);
			} catch (IOException e) {
				throw new RuntimeException("Could not read command configuration file", e);
			}
			LOGGER.info("Configured to accept commands from authorized API keys.");
		}
		commandHandler = new CommandHandler(
				config,
				solarThingDatabase,
				ZoneId.systemDefault() // graphql program doesn't have a way to configure time zone *yet*
		);
	}
	@Bean
	public CommandHandler commandHandler() {
		return commandHandler;
	}
}
