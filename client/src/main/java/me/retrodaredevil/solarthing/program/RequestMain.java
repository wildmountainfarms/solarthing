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
	public static int startRequestProgram(RequestProgramOptions options, File dataDirectory) throws Exception {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning request program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.REQUEST);
		return startRequestProgram(options, analyticsManager, options.getDataRequesterList(), options.getPeriod(), options.getMinimumWait());
	}

	public static <T extends PacketHandlingOption & CommandOption> int startRequestProgram(T options, AnalyticsManager analyticsManager, List<DataRequester> dataRequesterList, long period, long minimumWait) throws Exception {

		List<PacketListReceiver> packetListReceiverList = new ArrayList<>();
		for (DataRequester dataRequester : dataRequesterList) {
			DataRequesterResult result = dataRequester.createPacketListReceiver(new RequestObject(bundle.getEventHandler().getPacketListReceiverAccepter()));
			packetListReceiverList.add(result.getStatusPacketListReceiver());
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
