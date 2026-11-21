package me.retrodaredevil.solarthing.rest.command;

import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.util.NanoTimeProvider;
import me.retrodaredevil.solarthing.actions.CommonActionUtil;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.ClockEnvironment;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Clock;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;

@NullMarked
public class CommandHandler {
	private static final ObjectMapper CONFIG_MAPPER = CommonActionUtil.registerActionNodes(JacksonUtil.defaultMapperBuilder()).build();

	private final RestCommandConfig restCommandConfig;
	private final SolarThingDatabase solarThingDatabase;
	private final Clock clock;

	public CommandHandler(RestCommandConfig restCommandConfig, SolarThingDatabase solarThingDatabase, Clock clock) {
		this.restCommandConfig = restCommandConfig;
		this.solarThingDatabase = solarThingDatabase;
		this.clock = clock;
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
		// TODO put some of this logic in the common module
		try (InputStream inputStream = Files.newInputStream(command.getActionFile())){
			// TODO [Interpolate]? We still need to decide how we want to allow the possibility
			// TODO we want to allow NotationScript formatted files to be used
			return CONFIG_MAPPER.readValue(inputStream, ActionNode.class);
		} catch (IOException | JacksonException e) {
			throw new RuntimeException("Could not json from file: " + command.getActionFile(), e);
		}
	}
	public InjectEnvironment createInjectEnvironment(String commandName) {
		var builder = new InjectEnvironment.Builder()
				.add(new NanoTimeProviderEnvironment(NanoTimeProvider.SYSTEM_NANO_TIME))
				.add(new SolarThingDatabaseEnvironment(solarThingDatabase))
				.add(new ClockEnvironment(clock))
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
