package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.DataSource;
import me.retrodaredevil.solarthing.PacketGroupReceiver;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.MateCommandEnvironment;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.PacketHandlingOption;
import me.retrodaredevil.solarthing.couchdb.CouchDbDocumentKeyMap;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetrieverHandler;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import org.ektorp.ViewQuery;

import java.util.*;

public class CommandUtil {

	public static List<PacketHandler> getCommandRequesterHandlerList(List<DatabaseConfig> databaseConfigs, PacketGroupReceiver packetGroupReceiver, PacketHandlingOption options) {
		final List<PacketHandler> commandRequesterHandlerList = new ArrayList<>(); // Handlers to request and get new commands to send (This may block the current thread). (This doesn't actually handle packets)
		for(DatabaseConfig config : databaseConfigs){
			if(CouchDbDatabaseSettings.TYPE.equals(config.getType())){
				CouchDbDatabaseSettings settings = (CouchDbDatabaseSettings) config.getSettings();
				CouchProperties couchProperties = settings.getCouchProperties();

				IndividualSettings individualSettings = config.getIndividualSettingsOrDefault(Constants.DATABASE_COMMAND_DOWNLOAD_ID, null);
				FrequencySettings frequencySettings = individualSettings != null ? individualSettings.getFrequencySettings() : FrequencySettings.NORMAL_SETTINGS;
				commandRequesterHandlerList.add(new ThrottleFactorPacketHandler(new PrintPacketHandleExceptionWrapper(
						new CouchDbPacketRetrieverHandler(
								new CouchDbPacketRetriever(
										couchProperties,
										SolarThingConstants.OPEN_UNIQUE_NAME
								) {
									@Override
									protected ViewQuery alterView(ViewQuery view) {
										return super.alterView(view).startKey(System.currentTimeMillis() - 5 * 60 * 1000); // last 5 minutes
									}
								},
								new SecurityPacketReceiver(
										CouchDbDocumentKeyMap.createDefault(couchProperties),
										packetGroupReceiver,
										options.getSourceId(), options.getFragmentId(),
										Collections.singleton(CommandOpenPacket.class)
								)
						)
				), frequencySettings, true));
			}
		}
		return commandRequesterHandlerList;
	}
}
