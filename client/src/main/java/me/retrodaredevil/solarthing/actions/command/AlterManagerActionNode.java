package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.actions.environment.*;
import me.retrodaredevil.solarthing.commands.util.CommandManager;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.cache.DatabaseCache;

import java.io.File;

public class AlterManagerActionNode implements ActionNode {

	private final String sender;
	private final File keyDirectory;
	private final int fragmentId;

	public AlterManagerActionNode(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "key_directory", required = true) File keyDirectory,
			@JsonProperty(value = "fragment", required = true) int fragmentId) {
		this.sender = sender;
		this.keyDirectory = keyDirectory;
		this.fragmentId = fragmentId;
	}
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SolarThingDatabaseEnvironment solarThingDatabaseEnvironment = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class);
		OpenDatabaseCacheEnvironment openDatabaseCacheEnvironment = actionEnvironment.getInjectEnvironment().get(OpenDatabaseCacheEnvironment.class);
		AuthorizationEnvironment authorizationEnvironment = actionEnvironment.getInjectEnvironment().get(AuthorizationEnvironment.class);
		AlterPacketsEnvironment alterPacketsEnvironment = actionEnvironment.getInjectEnvironment().get(AlterPacketsEnvironment.class);
		SourceIdEnvironment sourceIdEnvironment = actionEnvironment.getInjectEnvironment().get(SourceIdEnvironment.class);
		TimeZoneEnvironment timeZoneEnvironment = actionEnvironment.getInjectEnvironment().get(TimeZoneEnvironment.class);

		SolarThingDatabase database = solarThingDatabaseEnvironment.getSolarThingDatabase();
		DatabaseCache openDatabaseCache = openDatabaseCacheEnvironment.getOpenDatabaseCache();
		String sourceId = sourceIdEnvironment.getSourceId();

		AlterPacketsProvider alterPacketsProvider = alterPacketsEnvironment.getAlterPacketsProvider();

		return new AlterManagerAction(
				new CommandManager(keyDirectory, sender),
				authorizationEnvironment.getPublicKeyLookUp(),
				database,
				openDatabaseCache,
				alterPacketsProvider,
				sourceId,
				timeZoneEnvironment.getZoneId(),
				fragmentId
		);
	}
}
