package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.ViewQueryParamsBuilder;
import me.retrodaredevil.solarthing.PacketGroupReceiver;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.PacketHandlingOption;
import me.retrodaredevil.solarthing.couchdb.CouchDbDocumentKeyMap;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetrieverHandler;
import me.retrodaredevil.solarthing.packets.handling.FrequencySettings;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.handling.PrintPacketHandleExceptionWrapper;
import me.retrodaredevil.solarthing.packets.handling.ThrottleFactorPacketHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandUtil {

	public static List<PacketHandler> getCommandRequesterHandlerList(List<DatabaseConfig> databaseConfigs, PacketGroupReceiver packetGroupReceiver, PacketHandlingOption options) {
		final List<PacketHandler> commandRequesterHandlerList = new ArrayList<>(); // Handlers to request and get new commands to send (This may block the current thread). (This doesn't actually handle packets)
		for(DatabaseConfig config : databaseConfigs){
			if(CouchDbDatabaseSettings.TYPE.equals(config.getType())){
				CouchDbDatabaseSettings settings = (CouchDbDatabaseSettings) config.getSettings();
				CouchDbInstance instance = CouchDbUtil.createInstance(settings.getCouchProperties(), settings.getOkHttpProperties());

				IndividualSettings individualSettings = config.getIndividualSettingsOrDefault(Constants.DATABASE_COMMAND_DOWNLOAD_ID, null);
				FrequencySettings frequencySettings = individualSettings != null ? individualSettings.getFrequencySettings() : FrequencySettings.NORMAL_SETTINGS;
				commandRequesterHandlerList.add(new ThrottleFactorPacketHandler(new PrintPacketHandleExceptionWrapper(
						new CouchDbPacketRetrieverHandler(
								new CouchDbPacketRetriever(
										instance.getDatabase(SolarThingConstants.OPEN_UNIQUE_NAME),
										() -> new ViewQueryParamsBuilder()
												.startKey(System.currentTimeMillis() - 5 * 60 * 1000)
												.build()
								),
								new SecurityPacketReceiver(
										CouchDbDocumentKeyMap.createDefault(instance),
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
