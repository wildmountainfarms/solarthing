package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdaterMultiplexer;
import me.retrodaredevil.solarthing.actions.environment.MateCommandEnvironment;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.analytics.MateAnalyticsHandler;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsListUpdater;
import me.retrodaredevil.solarthing.config.options.MateProgramOptions;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;
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
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.time.DailyIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class OutbackMateMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(OutbackMateMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private static final Collection<MateCommand> ALLOWED_COMMANDS = EnumSet.of(MateCommand.AUX_OFF, MateCommand.AUX_ON, MateCommand.USE, MateCommand.DROP);

	private static IOBundle createIOBundle(MateProgramOptions options) throws Exception {
		final IOBundle createdIO = ConfigUtil.createIOBundle(options.getIOBundleFile(), OutbackConstants.MATE_CONFIG);
		if(options.hasCommands()){
			return createdIO;
		}
		// just a simple safe guard to stop people from accessing the OutputStream if this program becomes more complex in the future
		return new IOBundle() {
			@Override public InputStream getInputStream() { return createdIO.getInputStream(); }
			@Override public OutputStream getOutputStream() { throw new IllegalStateException("You cannot access the output stream while commands are disabled!"); }
			@Override public void close() throws Exception { createdIO.close(); }
		};
	}

	@SuppressWarnings("SameReturnValue")
	public static int connectMate(MateProgramOptions options, File dataDirectory) throws Exception {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning mate program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.MATE);
		LOGGER.debug("IO Bundle File: " + options.getIOBundleFile());
		try(IOBundle io = createIOBundle(options)) {

			EnvironmentUpdater[] environmentUpdaterReference = new EnvironmentUpdater[1];
			PacketHandlerInit.Result handlersResult = PacketHandlerInit.initHandlers(
					options,
					() -> environmentUpdaterReference[0],
					Collections.singleton(new MateAnalyticsHandler(analyticsManager))
			);
			PacketListReceiverHandlerBundle bundle = handlersResult.getBundle();


			List<PacketListReceiver> packetListReceiverList = new ArrayList<>(Arrays.asList(
					OutbackDuplicatePacketRemover.INSTANCE,
					new FXEventUpdaterListReceiver(bundle.getEventHandler().getPacketListReceiverAccepter(), options.getFXWarningIgnoreMap()),
					new MXEventUpdaterListReceiver(bundle.getEventHandler().getPacketListReceiverAccepter()),
					new FXStatusListUpdater(new DailyIdentifier(options.getTimeZone()))
			));
			List<EnvironmentUpdater> environmentUpdaters = new ArrayList<>();
			for (DataRequester dataRequester : options.getDataRequesterList()) {
				DataRequesterResult result = dataRequester.create(new RequestObject(bundle.getEventHandler().getPacketListReceiverAccepter()));
				packetListReceiverList.add(result.getStatusPacketListReceiver());
				environmentUpdaters.add(result.getEnvironmentUpdater());
			}
			final List<CommandProvider<MateCommand>> commandProviders;
			if (options.hasCommands()) {
				packetListReceiverList.add(new AvailableCommandsListUpdater(options.getCommandInfoList()));

				Queue<SourcedCommand<MateCommand>> queue = new LinkedList<>();
				commandProviders = new ArrayList<>(); // if there are no commands, this should remain empty
				final CommandProvider<MateCommand> commandProvider = () -> {
					handlersResult.getUpdateCommandActions().run();
					return queue.poll();
				};
				commandProviders.add(commandProvider);

				environmentUpdaters.add((dataSource, injectEnvironmentBuilder) -> {
					injectEnvironmentBuilder.add(new MateCommandEnvironment(dataSource.toString(), queue));
				});
			} else {
				commandProviders = Collections.emptyList();
			}
			environmentUpdaterReference[0] = new EnvironmentUpdaterMultiplexer(environmentUpdaters);

			packetListReceiverList.add(new DataIdentifiablePacketListChecker());
			packetListReceiverList.add(new DaySummaryLogListReceiver());
			packetListReceiverList.addAll(bundle.createDefaultPacketListReceivers());

			SolarMain.initReader(
					requireNonNull(io.getInputStream()),
					new MatePacketCreator49(MateProgramOptions.getIgnoreCheckSum(options)),
					new TimedPacketReceiver(
							Duration.ofMillis(250),
							new PacketListReceiverMultiplexer(packetListReceiverList),
							new MateCommandSender(
									new CommandProviderMultiplexer<>(commandProviders), // if commands aren't allowed, commandProviders will be empty, so this will do nothing
									io.getOutputStream(),
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
		return 0;
	}
}
