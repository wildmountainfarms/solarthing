package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsListUpdater;
import me.retrodaredevil.solarthing.config.options.CommandOption;
import me.retrodaredevil.solarthing.config.options.PacketHandlingOption;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.config.options.RequestProgramOptions;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiablePacketListChecker;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.solar.DaySummaryLogListReceiver;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class RequestMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();
	public static int startRequestProgram(RequestProgramOptions options, File dataDirectory) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning request program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.REQUEST);
		return startRequestProgram(options, analyticsManager, options.getDataRequesterList(), options.getPeriod(), options.getMinimumWait());
	}

	public static <T extends PacketHandlingOption & CommandOption> int startRequestProgram(T options, AnalyticsManager analyticsManager, List<DataRequester> dataRequesterList, long period, long minimumWait) {
		List<DatabaseConfig> databaseConfigs = ConfigUtil.getDatabaseConfigs(options);
		PacketHandlerBundle packetHandlerBundle = PacketHandlerInit.getPacketHandlerBundle(databaseConfigs, SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME, options.getSourceId(), options.getFragmentId());
		List<PacketHandler> statusPacketHandlers = new ArrayList<>(packetHandlerBundle.getStatusPacketHandlers());


		final Map<String, ActionNode> actionNodeMap;
		EnvironmentUpdater[] environmentUpdaterReference = new EnvironmentUpdater[1];
		if (options.hasCommands()) {
			LatestPacketHandler latestPacketHandler = new LatestPacketHandler(false); // this is used to determine the state of the system when a command is requested
			statusPacketHandlers.add(latestPacketHandler);

			try {
				actionNodeMap = ActionUtil.getActionNodeMap(MAPPER, options);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			ActionNodeDataReceiver commandReceiver = new ActionNodeDataReceiver(
					actionNodeMap,
					(dataSource, injectEnvironmentBuilder) -> {
						injectEnvironmentBuilder
								.add(new TimeZoneEnvironment(options.getTimeZone()))
								.add(new LatestPacketGroupEnvironment(latestPacketHandler::getLatestPacketCollection))
								;
						environmentUpdaterReference[0].updateInjectEnvironment(dataSource, injectEnvironmentBuilder);
					}
			);

			statusPacketHandlers.add((packetCollection, instantType) -> commandReceiver.getActionUpdater().update());

			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Command are enabled!");
			List<PacketHandler> commandPacketHandlers = CommandUtil.getCommandRequesterHandlerList(databaseConfigs, commandReceiver, options);
			statusPacketHandlers.add(new PacketHandlerMultiplexer(commandPacketHandlers));
		} else {
			actionNodeMap = Collections.emptyMap();
		}

//		List<PacketHandler> eventPacketHandlers = new ArrayList<>(packetHandlerBundle.getEventPacketHandlers());

		PacketListReceiverHandlerBundle bundle = PacketListReceiverHandlerBundle.createFrom(options, packetHandlerBundle, statusPacketHandlers);

		List<PacketListReceiver> packetListReceiverList = new ArrayList<>();
		for (DataRequester dataRequester : dataRequesterList) {
			DataRequesterResult result = dataRequester.createPacketListReceiver(new RequestObject(bundle.getEventHandler().getPacketListReceiverAccepter()));
			packetListReceiverList.add();
		}
		if (options.hasCommands()) {
			packetListReceiverList.add(new AvailableCommandsListUpdater(options.getCommandInfoList()));
		}
		// now we are done adding PacketListReceivers to packetListReceiverList that update the list
		packetListReceiverList.add(new DataIdentifiablePacketListChecker());
		packetListReceiverList.add(new DaySummaryLogListReceiver());
		packetListReceiverList.addAll(bundle.createDefaultPacketListReceivers());
		PacketListReceiver packetListReceiver = new PacketListReceiverMultiplexer(packetListReceiverList);
		while (!Thread.currentThread().isInterrupted()) {
			long startTime = System.currentTimeMillis();
			List<Packet> packets = new ArrayList<>();
			packetListReceiver.receive(packets, InstantType.INSTANT);
			long timeTaken = System.currentTimeMillis() - startTime;

			long sleepTime = Math.max(minimumWait, period - timeTaken);
			LOGGER.debug("Going to sleep for " + sleepTime + "ms");
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		LOGGER.info("Ending program. Must have been interrupted.");
		return 1;
	}


}
