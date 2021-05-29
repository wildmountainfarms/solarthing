package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.solarthing.DataSource;
import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
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
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
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
	private void todo(){
		// TODO
		Queue<SourcedCommand<MateCommand>> queue = new LinkedList<>();
		List<CommandProvider<MateCommand>> commandProviders = new ArrayList<>(); // if there are no commands, this should remain empty
		final CommandProvider<MateCommand> commandProvider = () -> {
//			actionNodeDataReceiver.getActionUpdater().update(); won't need this because we'll update actions correctly
			return queue.poll();
		};
		commandProviders.add(commandProvider);

//		.add(new MateCommandEnvironment(dataSource.toString(), queue))
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
			List<PacketHandler> statusPacketHandlers = new ArrayList<>();

			EnvironmentUpdater[] environmentUpdaterReference = new EnvironmentUpdater[1];
			if(options.hasCommands()) {
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Command are enabled!");
				LatestPacketHandler latestPacketHandler = new LatestPacketHandler(false); // this is used to determine the state of the system when a command is requested
				statusPacketHandlers.add(latestPacketHandler);

				Map<String, ActionNode> actionNodeMap = ActionUtil.getActionNodeMap(MAPPER, options);
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

				final List<PacketHandler> commandPacketHandlers = CommandUtil.getCommandRequesterHandlerList(databaseConfigs, commandReceiver, options); // Handlers to request and get new commands to send (This may block the current thread). (This doesn't actually handle packets)
				statusPacketHandlers.add(new PacketHandlerMultiplexer(commandPacketHandlers));
			} else {
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Commands are disabled");
			}
			statusPacketHandlers.addAll(packetHandlerBundle.getStatusPacketHandlers());
			statusPacketHandlers.add(new MateAnalyticsHandler(analyticsManager));
			// now we're done updating statusPacketHandlers

			PacketListReceiverHandlerBundle bundle = PacketListReceiverHandlerBundle.createFrom(options, packetHandlerBundle, statusPacketHandlers);

			List<PacketListReceiver> packetListReceiverList = new ArrayList<>(Arrays.asList(
					OutbackDuplicatePacketRemover.INSTANCE,
					new FXEventUpdaterListReceiver(eventPacketListReceiverHandler.getPacketListReceiverAccepter(), options.getFXWarningIgnoreMap()),
					new MXEventUpdaterListReceiver(eventPacketListReceiverHandler.getPacketListReceiverAccepter()),
					new FXStatusListUpdater(new DailyIdentifier(options.getTimeZone())),
					new DaySummaryLogListReceiver()
			));
			for (DataRequester dataRequester : options.getDataRequesterList()) {
				DataRequesterResult result = dataRequester.createPacketListReceiver(new RequestObject(bundle.getEventHandler().getPacketListReceiverAccepter()));
				packetListReceiverList.add(result.getStatusPacketListReceiver());
			}
			if (options.hasCommands()) {
				packetListReceiverList.add(new AvailableCommandsListUpdater(options.getCommandInfoList()));
			}
			packetListReceiverList.addAll(bundle.createDefaultPacketListReceivers());
			SolarMain.initReader(
					requireNonNull(io.getInputStream()),
					new MatePacketCreator49(MateProgramOptions.getIgnoreCheckSum(options)),
					new TimedPacketReceiver(
							250,
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
