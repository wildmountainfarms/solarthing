package me.retrodaredevil.solarthing.rest.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.util.NanoTimeProvider;
import me.retrodaredevil.solarthing.actions.CommonActionUtil;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.IOException;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;

public class CommandHandler {
	private static final ObjectMapper CONFIG_MAPPER = CommonActionUtil.registerActionNodes(JacksonUtil.defaultMapper());

	private final RestCommandConfig restCommandConfig;
	private final SolarThingDatabase solarThingDatabase;
	private final ZoneId zoneId;

	public CommandHandler(RestCommandConfig restCommandConfig, SolarThingDatabase solarThingDatabase, ZoneId zoneId) {
		this.restCommandConfig = restCommandConfig;
		this.solarThingDatabase = solarThingDatabase;
		this.zoneId = zoneId;
	}


	public boolean isAuthorized(String apiKey) {
		requireNonNull(apiKey);
		return restCommandConfig.getApiKeys().contains(apiKey);
	}
	public @Nullable ActionNode getActionNode(String commandName) {
		RestCommandConfig.Command command = restCommandConfig.getCommandToActionFileMap().get(commandName);
		if (command == null) {
			return null;
		}
		try {
			return CONFIG_MAPPER.readValue(command.getActionFile(), ActionNode.class);
		} catch (IOException e) {
			throw new RuntimeException("Could not json from file: " + command.getActionFile(), e);
		}
	}
	public InjectEnvironment createInjectEnvironment(String commandName) {
		var builder = new InjectEnvironment.Builder()
				.add(new NanoTimeProviderEnvironment(NanoTimeProvider.SYSTEM_NANO_TIME))
				.add(new SolarThingDatabaseEnvironment(solarThingDatabase))
				.add(new TimeZoneEnvironment(zoneId))
				;
		RestCommandConfig.Command command = restCommandConfig.getCommandToActionFileMap().get(commandName);
		if (command == null) {
			throw new IllegalStateException("You should not be calling createInjectEnvironment if the command doesn't exist for commandName: " + commandName);
		}
		String sourceId = command.getSourceId();
		if (sourceId != null) {
			builder.add(new SourceIdEnvironment(sourceId));
		}
		return builder.build();
	}
}
