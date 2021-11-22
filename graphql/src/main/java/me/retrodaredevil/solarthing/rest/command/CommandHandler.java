package me.retrodaredevil.solarthing.rest.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.CommonActionUtil;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class CommandHandler {
	private static final ObjectMapper CONFIG_MAPPER = CommonActionUtil.registerActionNodes(JacksonUtil.defaultMapper());

	private final Set<String> authorizedApiKeys;
	private final Map<String, File> commandToActionFileMap;
	private final SolarThingDatabase solarThingDatabase;
	private final ZoneId zoneId;

	public CommandHandler(Set<String> authorizedApiKeys, Map<String, File> commandToActionFileMap, SolarThingDatabase solarThingDatabase, ZoneId zoneId) {
		this.authorizedApiKeys = authorizedApiKeys;
		this.commandToActionFileMap = commandToActionFileMap;
		this.solarThingDatabase = solarThingDatabase;
		this.zoneId = zoneId;
	}


	public boolean isAuthorized(String apiKey) {
		requireNonNull(apiKey);
		return authorizedApiKeys.contains(apiKey);
	}
	public @Nullable ActionNode getActionNode(String commandName) {
		File file = commandToActionFileMap.get(commandName);
		if (file == null) {
			return null;
		}
		try {
			return CONFIG_MAPPER.readValue(file, ActionNode.class);
		} catch (IOException e) {
			throw new RuntimeException("Could not json from file: " + file, e);
		}
	}
	public InjectEnvironment createInjectEnvironment(String sourceId) {
		return new InjectEnvironment.Builder()
				.add(new SolarThingDatabaseEnvironment(solarThingDatabase))
				.add(new SourceIdEnvironment(sourceId))
				.add(new TimeZoneEnvironment(zoneId))
				.build();
	}
}
