package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.action.node.util.NanoTimeProvider;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdaterMultiplexer;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.analytics.RoverAnalyticsHandler;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsListUpdater;
import me.retrodaredevil.solarthing.config.options.ActionsOption;
import me.retrodaredevil.solarthing.config.options.PacketHandlingOption;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.config.options.RequestProgramOptions;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiablePacketListChecker;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiverMultiplexer;
import me.retrodaredevil.solarthing.program.receiver.RoverEventUpdaterListReceiver;
import me.retrodaredevil.solarthing.program.receiver.TracerEventUpdaterListReceiver;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.reason.PacketCollectionExecutionReason;
import me.retrodaredevil.solarthing.solar.DaySummaryLogListReceiver;
import me.retrodaredevil.solarthing.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class RequestMain {
	private RequestMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMain.class);

	public static int startRequestProgram(RequestProgramOptions options, File dataDirectory) throws Exception {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning request program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.REQUEST);
		return startRequestProgram(options, analyticsManager, options.getPeriod(), options.getMinimumWait());
	}

	private static int startRequestProgram(RequestProgramOptions options, AnalyticsManager analyticsManager, Duration period, Duration minimumWait) throws Exception {
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
//		packetListReceiverList.add(createActionExecutorPacketListReceiver(options, environmentUpdaterReference[0])); part of the first draft of adding local actions
		dataRequesterResults.stream().map(DataRequesterResult::getStatusEndPacketListReceiver).forEachOrdered(packetListReceiverList::add);
		packetListReceiverList.addAll(bundle.createDefaultPacketListReceivers());

		return doRequest(new PacketListReceiverMultiplexer(packetListReceiverList), period, minimumWait);
	}
	private static <T extends ActionsOption & PacketHandlingOption> PacketHandler createActionExecutorPacketHandler(T options, EnvironmentUpdater environmentUpdater) throws IOException {
		List<ActionNode> actionNodes = ActionUtil.getActionNodes(options);
		requireNonNull(environmentUpdater);

		VariableEnvironment variableEnvironment = new VariableEnvironment();

		ActionMultiplexer multiplexer = new Actions.ActionMultiplexerBuilder().build();

		return packetCollection -> {
			InjectEnvironment.Builder injectEnvironmentBuilder = new InjectEnvironment.Builder()
					.add(new NanoTimeProviderEnvironment(NanoTimeProvider.SYSTEM_NANO_TIME))
					.add(new SourceIdEnvironment(options.getSourceId()))
					.add(new TimeZoneEnvironment(options.getZoneId()))
					.add(new LatestPacketGroupEnvironment(() -> packetCollection))
					;
			ExecutionReason executionReason = new PacketCollectionExecutionReason(packetCollection.getDateMillis(), packetCollection.getDbId());
			environmentUpdater.updateInjectEnvironment(executionReason, injectEnvironmentBuilder);
			InjectEnvironment injectEnvironment = injectEnvironmentBuilder.build();

			for (ActionNode actionNode : actionNodes) {
				multiplexer.add(actionNode.createAction(new ActionEnvironment(variableEnvironment, new VariableEnvironment(), injectEnvironment)));
			}
			multiplexer.update();
		};
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
