package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.DataSource;
import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.MateCommandEnvironment;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.analytics.MateAnalyticsHandler;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.ExtraOptionFlag;
import me.retrodaredevil.solarthing.config.options.MateProgramOptions;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetrieverHandler;
import me.retrodaredevil.solarthing.misc.device.RaspberryPiCpuTemperatureListUpdater;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.packets.handling.implementations.TimedPacketReceiver;
import me.retrodaredevil.solarthing.packets.security.crypto.DirectoryKeyMap;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import me.retrodaredevil.solarthing.solar.outback.OutbackDuplicatePacketRemover;
import me.retrodaredevil.solarthing.solar.outback.OutbackListUpdater;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.fx.FXEventUpdaterListReceiver;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingSettings;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingUpdaterListReceiver;
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
	private static final SerialConfig MATE_CONFIG = new SerialConfigBuilder(19200)
			.setDataBits(8)
			.setParity(SerialConfig.Parity.NONE)
			.setStopBits(SerialConfig.StopBits.ONE)
			.setDTR(true)
			.build();

	@SuppressWarnings("SameReturnValue")
	public static int connectMate(MateProgramOptions options, File dataDirectory) throws Exception {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning mate program");
		PacketCollectionIdGenerator statusIdGenerator = SolarMain.createIdGenerator(options.getUniqueIdsInOneHour());
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.MATE);
		LOGGER.debug("IO Bundle File: " + options.getIOBundleFile());
		final IOBundle io;
		{
			final IOBundle createdIO = SolarMain.createIOBundle(options.getIOBundleFile(), MATE_CONFIG);
			if(options.isAllowCommands()){
				io = createdIO;
			} else {
				// just a simple safe guard to stop people from accessing the OutputStream if this program becomes more complex in the future
				io = new IOBundle() {
					@Override public InputStream getInputStream() { return createdIO.getInputStream(); }
					@Override public OutputStream getOutputStream() { throw new IllegalStateException("You cannot access the output stream while commands are disabled!"); }
					@Override public void close() throws Exception { createdIO.close(); }
				};
			}
		}
		List<DatabaseConfig> databaseConfigs = SolarMain.getDatabaseConfigs(options);
		PacketHandlerBundle packetHandlerBundle = PacketHandlerInit.getPacketHandlerBundle(databaseConfigs, SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME);
		boolean rpiCpuTemperature = options.getExtraOptionFlags().contains(ExtraOptionFlag.RPI_LOG_CPU_TEMPERATURE);

		PacketHandler eventPacketHandler = new PacketHandlerMultiplexer(packetHandlerBundle.getEventPacketHandlers());
		PacketListReceiver sourceAndFragmentUpdater = SolarMain.getSourceAndFragmentUpdater(options);
		PacketListReceiverHandler eventPacketListReceiverHandler = new PacketListReceiverHandler(
				new PacketListReceiverMultiplexer(
						sourceAndFragmentUpdater,
						(packets, wasInstant) -> {
							LOGGER.debug(SolarThingConstants.NO_CONSOLE, "Debugging event packets");
							try {
								LOGGER.debug(SolarThingConstants.NO_CONSOLE, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(packets));
							} catch (JsonProcessingException e) {
								LOGGER.debug("Never mind about that...", e);
							}
						}
				),
				eventPacketHandler,
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR
		);

		final OnDataReceive onDataReceive;
		List<PacketHandler> statusPacketHandlers = new ArrayList<>();
		if(options.isAllowCommands()) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Commands are allowed");
			List<CommandProvider<MateCommand>> commandProviders = new ArrayList<>();
			{ // InputStreamCommandProvider command_input.txt block
				// TODO make the file path customizable through json (a DatabaseConfig)
				File commandInputFile = new File("command_input.txt");
				Files.write(commandInputFile.toPath(), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
				InputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(commandInputFile);
				} catch (FileNotFoundException e) {
					LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "No command input file! We created the file, but for some reason it isn't there...");
				}
				if (fileInputStream != null) {
					commandProviders.add(InputStreamCommandProvider.createFrom(fileInputStream, "command_input.txt", EnumSet.allOf(MateCommand.class)));
				}
			}
			Map<String, ActionNode> actionNodeMap = new HashMap<>();
			for (Map.Entry<String, File> entry : options.getCommandFileMap().entrySet()) {
				String name = entry.getKey();
				File file = entry.getValue();
				final ActionNode actionNode = MAPPER.readValue(file, ActionNode.class);
				actionNodeMap.put(name, actionNode);
			}
			LOGGER.debug("actionNodeMap={}", actionNodeMap);

			final List<PacketHandler> commandRequesterHandlerList = new ArrayList<>(); // Handlers to request and get new commands to send (This may block the current thread). (This doesn't actually handle packets)
			for(DatabaseConfig config : databaseConfigs){
				if(CouchDbDatabaseSettings.TYPE.equals(config.getType())){
					CouchDbDatabaseSettings settings = (CouchDbDatabaseSettings) config.getSettings();
					CouchProperties couchProperties = settings.getCouchProperties();
					LatestPacketHandler latestPacketHandler = new LatestPacketHandler(true); // this is used to determine the state of the system when a command is requested
					statusPacketHandlers.add(latestPacketHandler);
					final ActionNodeDataReceiver<MateCommand> actionNodeDataReceiver;
					{
						actionNodeDataReceiver = new ActionNodeDataReceiver<MateCommand>(actionNodeMap) {
							@Override
							protected void updateInjectEnvironment(DataSource dataSource, InjectEnvironment.Builder injectEnvironmentBuilder) {
								injectEnvironmentBuilder
										.add(new MateCommandEnvironment(dataSource.toString(), queue))
										.add(new LatestPacketGroupEnvironment(latestPacketHandler::getLatestPacketCollection));
							}
						};
					}
					commandProviders.add(actionNodeDataReceiver.getCommandProvider());

					IndividualSettings individualSettings = config.getIndividualSettingsOrDefault(Constants.DATABASE_COMMAND_DOWNLOAD_ID, null);
					FrequencySettings frequencySettings = individualSettings != null ? individualSettings.getFrequencySettings() : FrequencySettings.NORMAL_SETTINGS;
					commandRequesterHandlerList.add(new ThrottleFactorPacketHandler(new PrintPacketHandleExceptionWrapper(
							new CouchDbPacketRetrieverHandler(
									new CouchDbPacketRetriever(
											couchProperties,
											SolarThingConstants.COMMANDS_UNIQUE_NAME,
											true
									),
									new SecurityPacketReceiver(new DirectoryKeyMap(new File("authorized")), actionNodeDataReceiver, new DirectoryKeyMap(new File("unauthorized")))
							)
					), frequencySettings, true));
				}
			}

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
		PacketListReceiverHandler statusPacketListReceiverHandler = new PacketListReceiverHandler(
				new PacketListReceiverMultiplexer(
						sourceAndFragmentUpdater,
						(packets, wasInstant) -> {
							LOGGER.debug("Debugging all packets");
							try {
								LOGGER.debug(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(packets));
							} catch (JsonProcessingException e) {
								LOGGER.debug("Never mind about that...", e);
							}
						}
				),
				new PacketHandlerMultiplexer(statusPacketHandlers),
				statusIdGenerator
		);

		try {
			List<PacketListReceiver> packetListReceiverList = new ArrayList<>(Arrays.asList(
					OutbackDuplicatePacketRemover.INSTANCE,
					new FXEventUpdaterListReceiver(eventPacketListReceiverHandler.getPacketListReceiverAccepter(), options.getFXWarningIgnoreMap()),
					new MXEventUpdaterListReceiver(eventPacketListReceiverHandler.getPacketListReceiverAccepter()),
					new OutbackListUpdater(new DailyIdentifier(), eventPacketListReceiverHandler.getPacketListReceiverAccepter(), dataDirectory)
			));
			FXChargingSettings fxChargingSettings = options.getFXChargingSettings();
			if(fxChargingSettings != null){
				packetListReceiverList.add(new FXChargingUpdaterListReceiver(options.getMasterFXAddress(), fxChargingSettings));
			}
			if(rpiCpuTemperature){
				packetListReceiverList.add(new RaspberryPiCpuTemperatureListUpdater());
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
		} finally {
			io.close();
		}
		return 0;
	}
}
