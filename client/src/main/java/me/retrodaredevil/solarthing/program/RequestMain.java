package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdaterMultiplexer;
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
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiverMultiplexer;
import me.retrodaredevil.solarthing.solar.DaySummaryLogListReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RequestMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMain.class);

	public static int startRequestProgram(RequestProgramOptions options, File dataDirectory) throws Exception {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning request program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.REQUEST);
		return startRequestProgram(options, analyticsManager, options.getDataRequesterList(), options.getPeriod(), options.getMinimumWait());
	}

	public static <T extends PacketHandlingOption & CommandOption> int startRequestProgram(T options, AnalyticsManager analyticsManager, List<DataRequester> dataRequesterList, long period, long minimumWait) throws Exception {
		EnvironmentUpdater[] environmentUpdaterReference = new EnvironmentUpdater[1];
		PacketHandlerInit.Result handlersResult = PacketHandlerInit.initHandlers(options, () -> environmentUpdaterReference[0]);
		PacketListReceiverHandlerBundle bundle = handlersResult.getBundle();

		List<PacketListReceiver> packetListReceiverList = new ArrayList<>();
		List<EnvironmentUpdater> environmentUpdaters = new ArrayList<>();
		for (DataRequester dataRequester : dataRequesterList) {
			DataRequesterResult result = dataRequester.create(new RequestObject(bundle.getEventHandler().getPacketListReceiverAccepter()));
			packetListReceiverList.add(result.getStatusPacketListReceiver());
			environmentUpdaters.add(result.getEnvironmentUpdater());
		}
		if (options.hasCommands()) {
			packetListReceiverList.add(new AvailableCommandsListUpdater(options.getCommandInfoList()));
		}
		environmentUpdaterReference[0] = new EnvironmentUpdaterMultiplexer(environmentUpdaters);

		packetListReceiverList.add(new DataIdentifiablePacketListChecker());
		packetListReceiverList.add(new DaySummaryLogListReceiver());
		packetListReceiverList.addAll(bundle.createDefaultPacketListReceivers());

		return doRequest(new PacketListReceiverMultiplexer(packetListReceiverList), period, minimumWait);
	}
	private static int doRequest(PacketListReceiver packetListReceiver, long period, long minimumWait) {
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
