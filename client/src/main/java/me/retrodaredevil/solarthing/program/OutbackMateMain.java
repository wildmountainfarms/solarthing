package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.commands.sequence.CommandSequence;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.MateProgramOptions;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetrieverHandler;
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
		LOGGER.info("Beginning mate program");
		PacketCollectionIdGenerator statusIdGenerator = SolarMain.createIdGenerator(options.getUniqueIdsInOneHour());
		LOGGER.info("IO Bundle File: " + options.getIOBundleFile());
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

		PacketHandler eventPacketHandler = new PacketHandlerMultiplexer(packetHandlerBundle.getEventPacketHandlers());
		PacketListReceiver sourceAndFragmentUpdater = SolarMain.getSourceAndFragmentUpdater(options);
		PacketListReceiverHandler eventPacketListReceiverHandler = new PacketListReceiverHandler(
				new PacketListReceiverMultiplexer(
						sourceAndFragmentUpdater,
						(packets, wasInstant) -> {
							LOGGER.debug("Debugging event packets");
							try {
								LOGGER.debug(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(packets));
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
			LOGGER.info("Commands are allowed");
			List<CommandProvider<MateCommand>> commandProviders = new ArrayList<>();
			{ // InputStreamCommandProvider command_input.txt block
				// TODO make the file path customizable through json (a DatabaseConfig)
				File commandInputFile = new File("command_input.txt");
				Files.write(commandInputFile.toPath(), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
				InputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(commandInputFile);
				} catch (FileNotFoundException e) {
					LOGGER.warn("No command input file! We created the file, but for some reason it isn't there...");
				}
				if (fileInputStream != null) {
					commandProviders.add(InputStreamCommandProvider.createFrom(fileInputStream, "command_input.txt", EnumSet.allOf(MateCommand.class)));
				}
			}

			final List<PacketHandler> commandRequesterHandlerList = new ArrayList<>(); // Handlers to request and get new commands to send (This may block the current thread). (This doesn't actually handle packets)
//			final List<PacketHandler> commandFeedbackHandlerList = new ArrayList<>(); // Handlers to handle successful command packets, usually by storing those packets somewhere (May block the current thread)
			for(DatabaseConfig config : databaseConfigs){
				if(CouchDbDatabaseSettings.TYPE.equals(config.getType())){
					CouchDbDatabaseSettings settings = (CouchDbDatabaseSettings) config.getSettings();
					CouchProperties couchProperties = settings.getCouchProperties();
					LatestPacketHandler latestPacketHandler = new LatestPacketHandler(true);
					statusPacketHandlers.add(latestPacketHandler);
					final CommandSequenceDataReceiver<MateCommand> commandSequenceDataReceiver;
					{
						CommandSequence<MateCommand> generatorShutOff = CommandSequences.createAuxGeneratorShutOff(latestPacketHandler::getLatestPacketCollection);
						Map<String, CommandSequence<MateCommand>> map = new HashMap<>();
						map.put("GEN OFF", generatorShutOff);
						commandSequenceDataReceiver = new CommandSequenceDataReceiver<>(map);
					}
					commandProviders.add(commandSequenceDataReceiver.getCommandProvider());

					IndividualSettings individualSettings = config.getIndividualSettingsOrDefault(Constants.DATABASE_COMMAND_DOWNLOAD_ID, null);
					FrequencySettings frequencySettings = individualSettings != null ? individualSettings.getFrequencySettings() : FrequencySettings.NORMAL_SETTINGS;
					commandRequesterHandlerList.add(new ThrottleFactorPacketHandler(new PrintPacketHandleExceptionWrapper(
							new CouchDbPacketRetrieverHandler(
									new CouchDbPacketRetriever(
											couchProperties,
											SolarThingConstants.COMMANDS_UNIQUE_NAME,
											true
									),
									new SecurityPacketReceiver(new DirectoryKeyMap(new File("authorized")), commandSequenceDataReceiver, new DirectoryKeyMap(new File("unauthorized")))
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
			LOGGER.info("Commands are disabled");
			onDataReceive = OnDataReceive.Defaults.NOTHING;
		}
		statusPacketHandlers.addAll(packetHandlerBundle.getStatusPacketHandlers());
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
