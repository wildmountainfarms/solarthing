package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdaterMultiplexer;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.analytics.RoverAnalyticsHandler;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsListUpdater;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.config.options.RequestProgramOptions;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiablePacketListChecker;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiverMultiplexer;
import me.retrodaredevil.solarthing.program.receiver.RoverEventUpdaterListReceiver;
import me.retrodaredevil.solarthing.program.receiver.TracerEventUpdaterListReceiver;
import me.retrodaredevil.solarthing.solar.DaySummaryLogListReceiver;
import me.retrodaredevil.solarthing.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMain {
	private RequestMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMain.class);

	public static int startRequestProgram(RequestProgramOptions options, Path dataDirectory, boolean isValidate) throws Exception {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning request program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled() && !isValidate, dataDirectory);
		analyticsManager.sendStartUp(ProgramType.REQUEST);
		return startRequestProgram(options, analyticsManager, options.getPeriod(), options.getMinimumWait(), isValidate);
	}

	private static int startRequestProgram(RequestProgramOptions options, AnalyticsManager analyticsManager, Duration period, Duration minimumWait, boolean isValidate) throws Exception {
		// Note this is very similar to code in OutbackMateMain and could eventually be refactored
		EnvironmentUpdater[] environmentUpdaterReference = new EnvironmentUpdater[1];
		PacketHandlerInit.Result handlersResult = PacketHandlerInit.initHandlers(
				options,
				() -> environmentUpdaterReference[0],
				Collections.singleton(new RoverAnalyticsHandler(analyticsManager))
		);
		PacketListReceiverHandlerBundle bundle = handlersResult.getBundle();
		List<DataRequesterResult> dataRequesterResults = options.getDataRequesterList().stream()
				.map(dataRequester -> dataRequester.create(new RequestObject(bundle.getEventHandler().getPacketListReceiverAccepter())))
				.collect(Collectors.toList());

		List<PacketListReceiver> packetListReceiverList = new ArrayList<>();
		List<EnvironmentUpdater> environmentUpdaters = new ArrayList<>();

		dataRequesterResults.stream().map(DataRequesterResult::getStatusPacketListReceiver).forEachOrdered(packetListReceiverList::add);
		dataRequesterResults.stream().map(DataRequesterResult::getEnvironmentUpdater).forEachOrdered(environmentUpdaters::add);

		packetListReceiverList.add(new RoverEventUpdaterListReceiver(bundle.getEventHandler().getPacketListReceiverAccepter())); // will add events for each rover packet if there are any to add
		packetListReceiverList.add(new TracerEventUpdaterListReceiver(bundle.getEventHandler().getPacketListReceiverAccepter())); // will add events for each tracer packet if there are any to add
		if (options.hasCommands()) {
			packetListReceiverList.add(new AvailableCommandsListUpdater(options.getCommandInfoList(), false));
		}
		environmentUpdaterReference[0] = new EnvironmentUpdaterMultiplexer(environmentUpdaters);

		packetListReceiverList.add(new DataIdentifiablePacketListChecker());
		packetListReceiverList.add(new DaySummaryLogListReceiver());
		dataRequesterResults.stream().map(DataRequesterResult::getStatusEndPacketListReceiver).forEachOrdered(packetListReceiverList::add);
		packetListReceiverList.addAll(bundle.createDefaultPacketListReceivers());

		if (isValidate) {
			return 0;
		}
		return doRequest(new PacketListReceiverMultiplexer(packetListReceiverList), period, minimumWait);
	}
	private static int doRequest(PacketListReceiver packetListReceiver, Duration period, Duration minimumWait) {
		while (!Thread.currentThread().isInterrupted()) {
			long startTimeNanos = System.nanoTime();
			List<Packet> packets = new ArrayList<>();
			packetListReceiver.receive(packets);
			long timeTakenNanos = System.nanoTime() - startTimeNanos;

			long sleepTimeNanos = Math.max(minimumWait.toNanos(), period.toNanos() - timeTakenNanos);
			LOGGER.debug("Going to sleep for " + TimeUtil.nanosToSecondsString(sleepTimeNanos) + " seconds");
			try {
				Thread.sleep(sleepTimeNanos / 1_000_000, (int) (sleepTimeNanos % 1_000_000));
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		LOGGER.info("Ending program. Must have been interrupted.");
		return SolarThingConstants.EXIT_CODE_INTERRUPTED;
	}


}
