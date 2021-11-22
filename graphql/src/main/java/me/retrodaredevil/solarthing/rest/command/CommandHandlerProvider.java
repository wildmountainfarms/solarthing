package me.retrodaredevil.solarthing.rest.command;

import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;

@Component
public class CommandHandlerProvider {

	@Value("${solarthing.config.command.api_key:#{null}}")
	private @Nullable String commandApiKey;
	@Value("#{${solarthing.config.command.commands}}") // We have to have it like this in order to be able to define maps of data
	private @Nullable Map<String, File> commandNameToActionFileMap;

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
		commandHandler = new CommandHandler(
				commandApiKey == null ? Collections.emptySet() : Collections.singleton(commandApiKey),
				commandNameToActionFileMap,
				solarThingDatabase,
				ZoneId.systemDefault() // graphql program doesn't have a way to configure time zone *yet*
		);
	}
	@Bean
	public CommandHandler commandHandler() {
		return commandHandler;
	}
}
