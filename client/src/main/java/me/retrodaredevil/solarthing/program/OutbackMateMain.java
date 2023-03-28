package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdaterMultiplexer;
import me.retrodaredevil.solarthing.actions.environment.MateCommandEnvironment;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.analytics.MateAnalyticsHandler;
import me.retrodaredevil.solarthing.commands.command.CommandProvider;
import me.retrodaredevil.solarthing.commands.command.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.commands.command.SourcedCommand;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsListUpdater;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.options.MateProgramOptions;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.io.ReloadableIOBundle;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiablePacketListChecker;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiverMultiplexer;
import me.retrodaredevil.solarthing.packets.handling.implementations.TimedPacketReceiver;
import me.retrodaredevil.solarthing.solar.DaySummaryLogListReceiver;
import me.retrodaredevil.solarthing.solar.outback.FXStatusListUpdater;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import me.retrodaredevil.solarthing.solar.outback.OutbackConstants;
import me.retrodaredevil.solarthing.solar.outback.OutbackDuplicatePacketRemover;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.fx.FXEventUpdaterListReceiver;
import me.retrodaredevil.solarthing.solar.outback.mx.MXEventUpdaterListReceiver;
import me.retrodaredevil.solarthing.util.time.DailyIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class OutbackMateMain {
	private OutbackMateMain() { throw new UnsupportedOperationException(); }
	private static final Logger LOGGER = LoggerFactory.getLogger(OutbackMateMain.class);

	private static final Collection<MateCommand> ALLOWED_COMMANDS = EnumSet.of(MateCommand.AUX_OFF, MateCommand.AUX_ON, MateCommand.USE, MateCommand.DROP);

	@SuppressWarnings("SameReturnValue")
	public static int connectMate(MateProgramOptions options, Path dataDirectory, boolean isValidate) throws Exception {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning mate program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.MATE);
		LOGGER.debug("IO Bundle File: " + options.getIOBundleFilePath());
		IOConfig ioConfig = ConfigUtil.parseIOConfig(options.getIOBundleFilePath(), OutbackConstants.MATE_CONFIG);
		try(ReloadableIOBundle ioBundle = new ReloadableIOBundle(ioConfig::createIOBundle)) {

			// Note this is very similar to code in RequestMain and could eventually be refactored
			EnvironmentUpdater[] environmentUpdaterReference = new EnvironmentUpdater[1];
			PacketHandlerInit.Result handlersResult = PacketHandlerInit.initHandlers(
					options,
					() -> environmentUpdaterReference[0],
					Collections.singleton(new MateAnalyticsHandler(analyticsManager))
			);
			PacketListReceiverHandlerBundle bundle = handlersResult.getBundle();
			List<DataRequesterResult> dataRequesterResults = options.getDataRequesterList().stream()
					.map(dataRequester -> dataRequester.create(new RequestObject(bundle.getEventHandler().getPacketListReceiverAccepter())))
					.collect(Collectors.toList());


			List<PacketListReceiver> packetListReceiverList = new ArrayList<>(Arrays.asList(
					OutbackDuplicatePacketRemover.INSTANCE,
					new FXEventUpdaterListReceiver(bundle.getEventHandler().getPacketListReceiverAccepter(), options.getFXWarningIgnoreMap()),
					new MXEventUpdaterListReceiver(bundle.getEventHandler().getPacketListReceiverAccepter()),
					new FXStatusListUpdater(new DailyIdentifier(options.getZoneId()))
			));
			List<EnvironmentUpdater> environmentUpdaters = new ArrayList<>();

			dataRequesterResults.stream().map(DataRequesterResult::getStatusPacketListReceiver).forEachOrdered(packetListReceiverList::add);
			dataRequesterResults.stream().map(DataRequesterResult::getEnvironmentUpdater).forEachOrdered(environmentUpdaters::add);

			final List<CommandProvider<MateCommand>> commandProviders;
			if (options.hasCommands()) {
				packetListReceiverList.add(new AvailableCommandsListUpdater(options.getCommandInfoList(), false));

				Queue<SourcedCommand<MateCommand>> queue = new ArrayDeque<>();
				commandProviders = new ArrayList<>(); // if there are no commands, this should remain empty
				final CommandProvider<MateCommand> commandProvider = () -> {
					handlersResult.getUpdateCommandActions().run();
					return queue.poll();
				};
				commandProviders.add(commandProvider);

				environmentUpdaters.add((_executionReason, injectEnvironmentBuilder) -> {
					injectEnvironmentBuilder.add(new MateCommandEnvironment(queue));
				});
			} else {
				commandProviders = Collections.emptyList();
			}
			environmentUpdaterReference[0] = new EnvironmentUpdaterMultiplexer(environmentUpdaters);

			packetListReceiverList.add(new DataIdentifiablePacketListChecker());
			packetListReceiverList.add(new DaySummaryLogListReceiver());
			dataRequesterResults.stream().map(DataRequesterResult::getStatusEndPacketListReceiver).forEachOrdered(packetListReceiverList::add);
			packetListReceiverList.addAll(bundle.createDefaultPacketListReceivers());

			if (isValidate) {
				return 0;
			}
			return SolarMain.initReader(
					requireNonNull(ioBundle.getInputStream()),
					ioBundle::reload,
					new MatePacketCreator49(MateProgramOptions.getIgnoreCheckSum(options)),
					new TimedPacketReceiver(
							Duration.ofMillis(250),
							new PacketListReceiverMultiplexer(packetListReceiverList),
							new MateCommandSender(
									new CommandProviderMultiplexer<>(commandProviders), // if commands aren't allowed, commandProviders will be empty, so this will do nothing
									ioBundle.getOutputStream(),
									ALLOWED_COMMANDS,
									new OnMateCommandSent(new PacketListReceiverMultiplexer(
											bundle.getEventHandler().getPacketListReceiverAccepter(),
											bundle.getEventHandler().getPacketListReceiverPacker(),
											bundle.getEventHandler().getPacketListReceiverHandler()
									))
							)
					)
			);
		}
	}
}
