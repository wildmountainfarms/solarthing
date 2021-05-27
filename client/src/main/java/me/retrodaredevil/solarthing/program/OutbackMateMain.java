package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.solarthing.DataSource;
import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.MateCommandEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.analytics.MateAnalyticsHandler;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsListUpdater;
import me.retrodaredevil.solarthing.config.options.MateProgramOptions;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.handling.*;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class OutbackMateMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(OutbackMateMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

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
		PacketCollectionIdGenerator statusIdGenerator = SolarMain.createIdGenerator(options.getUniqueIdsInOneHour());
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.MATE);
		LOGGER.debug("IO Bundle File: " + options.getIOBundleFile());
		try(IOBundle io = createIOBundle(options)) {
			List<DatabaseConfig> databaseConfigs = ConfigUtil.getDatabaseConfigs(options);
			PacketHandlerBundle packetHandlerBundle = PacketHandlerInit.getPacketHandlerBundle(databaseConfigs, SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME, options.getSourceId(), options.getFragmentId());

			PacketListReceiverHandlerBundle bundle = PacketListReceiverHandlerBundle.createFrom(options, packetHandlerBundle, statusPacketHandlers);

			final OnDataReceive onDataReceive;
			List<PacketHandler> statusPacketHandlers = new ArrayList<>();
			if(options.hasCommands()) {
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Commands are allowed");
				List<CommandProvider<MateCommand>> commandProviders = new ArrayList<>();
				Map<String, ActionNode> actionNodeMap = ActionUtil.getActionNodeMap(MAPPER, options);
				LOGGER.debug("actionNodeMap={}", actionNodeMap);

				LatestPacketHandler latestPacketHandler = new LatestPacketHandler(false); // this is used to determine the state of the system when a command is requested
				statusPacketHandlers.add(latestPacketHandler);

				Queue<SourcedCommand<MateCommand>> queue = new LinkedList<>();
				final ActionNodeDataReceiver actionNodeDataReceiver = new ActionNodeDataReceiver(actionNodeMap, environmentUpdater) {
					@Override
					protected void updateInjectEnvironment(DataSource dataSource, InjectEnvironment.Builder injectEnvironmentBuilder) {
						injectEnvironmentBuilder
								.add(new TimeZoneEnvironment(options.getTimeZone()))
								.add(new MateCommandEnvironment(dataSource.toString(), queue))
								.add(new LatestPacketGroupEnvironment(latestPacketHandler::getLatestPacketCollection));
					}
				};
				final CommandProvider<MateCommand> commandProvider = () -> {
					actionNodeDataReceiver.getActionUpdater().update();
					return queue.poll();
				};
				commandProviders.add(commandProvider);
				final List<PacketHandler> commandRequesterHandlerList = CommandUtil.getCommandRequesterHandlerList(databaseConfigs, actionNodeDataReceiver, options); // Handlers to request and get new commands to send (This may block the current thread). (This doesn't actually handle packets)

				final PacketHandler commandRequesterHandler = new PacketHandlerMultiplexer(commandRequesterHandlerList);
				Collection<MateCommand> allowedCommands = EnumSet.of(MateCommand.AUX_OFF, MateCommand.AUX_ON, MateCommand.USE, MateCommand.DROP);
				onDataReceive = new MateCommandSender(
						new CommandProviderMultiplexer<>(commandProviders),
						io.getOutputStream(),
						allowedCommands,
						new OnMateCommandSent(new PacketListReceiverMultiplexer(
								eventPacketListReceiverHandler.getPacketListReceiverAccepter(),
								eventPacketListReceiverHandler.getPacketListReceiverPacker(),
								eventPacketListReceiverHandler.getPacketListReceiverHandler()
						))
				);
				statusPacketHandlers.add(commandRequesterHandler);
			} else {
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Commands are disabled");
				onDataReceive = OnDataReceive.Defaults.NOTHING;
			}
			statusPacketHandlers.addAll(packetHandlerBundle.getStatusPacketHandlers());
			statusPacketHandlers.add(new MateAnalyticsHandler(analyticsManager));

			List<PacketListReceiver> packetListReceiverList = new ArrayList<>(Arrays.asList(
					OutbackDuplicatePacketRemover.INSTANCE,
					new FXEventUpdaterListReceiver(eventPacketListReceiverHandler.getPacketListReceiverAccepter(), options.getFXWarningIgnoreMap()),
					new MXEventUpdaterListReceiver(eventPacketListReceiverHandler.getPacketListReceiverAccepter()),
					new FXStatusListUpdater(new DailyIdentifier(options.getTimeZone())),
					new DaySummaryLogListReceiver()
			));
			if (options.hasCommands()) {
				packetListReceiverList.add(new AvailableCommandsListUpdater(options.getCommandInfoList()));
			}
			for (DataRequester dataRequester : options.getDataRequesterList()) {
				packetListReceiverList.add(dataRequester.createPacketListReceiver(new RequestObject(eventPacketListReceiverHandler.getPacketListReceiverAccepter(), Collections.emptyMap())));
			}
			packetListReceiverList.addAll(Arrays.asList(
					statusPacketListReceiverHandler.getPacketListReceiverAccepter(),
					statusPacketListReceiverHandler.getPacketListReceiverPacker(),
					eventPacketListReceiverHandler.getPacketListReceiverPacker(),
					statusPacketListReceiverHandler.getPacketListReceiverHandler(),
					eventPacketListReceiverHandler.getPacketListReceiverHandler()
			));
			SolarMain.initReader(
					requireNonNull(io.getInputStream()),
					new MatePacketCreator49(MateProgramOptions.getIgnoreCheckSum(options)),
					new TimedPacketReceiver(
							250,
							new PacketListReceiverMultiplexer(packetListReceiverList),
							onDataReceive
					)
			);
		}
		return 0;
	}
}
