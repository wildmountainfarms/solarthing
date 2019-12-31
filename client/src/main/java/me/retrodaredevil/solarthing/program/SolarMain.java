package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.modbus.*;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.OnDataReceive;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.commands.sequence.CommandSequence;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.LatestFileDatabaseSettings;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.io.SerialIOConfig;
import me.retrodaredevil.solarthing.config.options.*;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.outhouse.OuthousePacketCreator;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.packets.handling.implementations.PacketHandlerPacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.implementations.TimedPacketReceiver;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.packets.security.crypto.DirectoryKeyMap;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import me.retrodaredevil.solarthing.solar.outback.OutbackDuplicatePacketRemover;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.fx.DailyFXListUpdater;
import me.retrodaredevil.solarthing.solar.renogy.rover.*;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveWrite;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.scheduler.MidnightIterativeScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static java.util.Objects.requireNonNull;

public final class SolarMain {
	private SolarMain(){ throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(SolarMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private static final SerialConfig MATE_CONFIG = new SerialConfigBuilder(19200)
			.setDataBits(8)
			.setParity(SerialConfig.Parity.NONE)
			.setStopBits(SerialConfig.StopBits.ONE)
			.setDTR(true)
			.build();
	private static final SerialConfig ROVER_CONFIG = new SerialConfigBuilder(9600)
			.setDataBits(8)
			.setParity(SerialConfig.Parity.NONE)
			.setStopBits(SerialConfig.StopBits.ONE)
			.build();

	@SuppressWarnings("SameReturnValue")
	private static int connectMate(MateProgramOptions options) throws Exception {
		LOGGER.info("Beginning mate program");
		PacketCollectionIdGenerator idGenerator = createIdGenerator(options.getUniqueIdsInOneHour());
		LOGGER.info("IO Bundle File: " + options.getIOBundleFile());
		final IOBundle io;
		{
			final IOBundle createdIO = createIOBundle(options.getIOBundleFile(), MATE_CONFIG);
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
		List<DatabaseConfig> databaseConfigs = getDatabaseConfigs(options);
		PacketHandlerBundle packetHandlerBundle = PacketHandlerInit.getPacketHandlerBundle(databaseConfigs, SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME);

		PacketHandler eventPacketHandler = new PacketHandlerMultiplexer(packetHandlerBundle.getEventPacketHandlers());
		PacketListReceiver sourceAndFragmentUpdater = getSourceAndFragmentUpdater(options);
		PacketListReceiverCollectorHandler packetListReceiverCollectorHandler = new PacketListReceiverCollectorHandler(
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
				eventPacketHandler
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
							new CouchDbPacketRetriever(
									couchProperties,
									SolarThingConstants.COMMANDS_UNIQUE_NAME,
									new SecurityPacketReceiver(new DirectoryKeyMap(new File("authorized")), commandSequenceDataReceiver, new DirectoryKeyMap(new File("unauthorized"))),
								  true
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
							packetListReceiverCollectorHandler.getPacketListReceiverAccepter(),
							packetListReceiverCollectorHandler.getPacketListReceiverHandler()
					))
			);
			statusPacketHandlers.add(commandRequesterHandler);
		} else {
			LOGGER.info("Commands are disabled");
			onDataReceive = OnDataReceive.Defaults.NOTHING;
		}
		statusPacketHandlers.addAll(packetHandlerBundle.getStatusPacketHandlers());

		try {
			initReader(
					requireNonNull(io.getInputStream()),
					new MatePacketCreator49(MateProgramOptions.getIgnoreCheckSum(options)),
					new TimedPacketReceiver(
							250,
							new PacketListReceiverMultiplexer(
									OutbackDuplicatePacketRemover.INSTANCE,
									new DailyFXListUpdater(new MidnightIterativeScheduler(), packetListReceiverCollectorHandler.getPacketListReceiverAccepter()),
									sourceAndFragmentUpdater,
									(packets, wasInstant) -> {
										LOGGER.debug("Debugging all packets");
										try {
											LOGGER.debug(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(packets));
										} catch (JsonProcessingException e) {
											LOGGER.debug("Never mind about that...", e);
										}
									},
									new PacketHandlerPacketListReceiver(new PacketHandlerMultiplexer(statusPacketHandlers), idGenerator),
									packetListReceiverCollectorHandler.getPacketListReceiverHandler()
							),
							onDataReceive
					)
			);
		} finally {
			io.close();
		}
		return 0;
	}
	private static int connectRover(RoverProgramOptions options) {
		LOGGER.info("Beginning rover program");
		PacketHandlerBundle packetHandlerBundle = PacketHandlerInit.getPacketHandlerBundle(getDatabaseConfigs(options), SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME);
		// TODO packetHandlerBundle.getEventPacketHandlers()
		List<PacketHandler> packetHandlers = new ArrayList<>(packetHandlerBundle.getStatusPacketHandlers());
		PacketHandler packetHandler = new PacketHandlerMultiplexer(packetHandlers);
		PacketListReceiver packetListReceiver = getSourceAndFragmentUpdater(options);


		PacketCollectionIdGenerator idGenerator = createIdGenerator(options.getUniqueIdsInOneHour());
		return doRoverProgram(options, (read, write) -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					final long startTime = System.currentTimeMillis();
					final RoverStatusPacket packet;
					try {
						packet = RoverStatusPackets.createFromReadTable(read);
					} catch(ModbusRuntimeException e){
						LOGGER.error("Modbus exception", e);
						Thread.sleep(5000);
						continue;
					}
					try {
						LOGGER.debug(MAPPER.writeValueAsString(packet) + "\n" +
								packet.getSpecialPowerControlE021().getFormattedInfo().replaceAll("\n", "\n\t") + "\n" +
								packet.getSpecialPowerControlE02D().getFormattedInfo().replaceAll("\n", "\n\t")
						);
					} catch (JsonProcessingException e) {
						LOGGER.debug("Tried debugging value...", e);
					}
					List<Packet> packets = new ArrayList<>();
					packets.add(packet);
					packetListReceiver.receive(packets, true);
					PacketCollection packetCollection = PacketCollections.createFromPackets(packets, idGenerator);
					final long readDuration = System.currentTimeMillis() - startTime;
					LOGGER.debug("took " + readDuration + "ms to read from Rover");
					final long saveStartTime = System.currentTimeMillis();
					try {
						packetHandler.handle(packetCollection, true);
					} catch (PacketHandleException e) {
						LOGGER.error("Couldn't save a renogy rover packet!", e);
					}
					final long saveDuration = System.currentTimeMillis() - saveStartTime;
					LOGGER.debug("took " + saveDuration + "ms to handle packets");
					Thread.sleep(Math.max(1000, 6000 - readDuration)); // allow 5 seconds to read from rover
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
	}
	private static int connectRoverSetup(RoverSetupProgramOptions options) {
		return doRoverProgram(options, RoverSetupProgram::startRoverSetup);
	}
	private static int doRoverProgram(RoverOption options, RoverProgramRunner runner) {
		File dummyFile = options.getDummyFile();
		if(dummyFile != null){
			final FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(dummyFile);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("The dummy file was not found!", e);
			}
			final RoverStatusPacket roverStatusPacket;
			try {
				roverStatusPacket = MAPPER.readValue(fileInputStream, RoverStatusPacket.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			DummyRoverReadWrite readWrite = new DummyRoverReadWrite(
				roverStatusPacket,
				(fieldName, previousValue, newValue) -> System.out.println(fieldName + " changed from " + previousValue + " to " + newValue)
			);
			runner.doProgram(readWrite, readWrite);
			return 0;
		} else {
			try(IOBundle ioBundle = createIOBundle(options.getIOBundleFile(), ROVER_CONFIG)) {
				ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RTUDataEncoder(2000, 20, 4));
				ModbusSlave slave = new ImmutableAddressModbusSlave(options.getModbusAddress(), modbus);
				RoverReadTable read = new RoverModbusSlaveRead(slave);
				RoverWriteTable write = new RoverModbusSlaveWrite(slave);
				runner.doProgram(read, write);
				return 0;
			} catch (Exception e) {
				LOGGER.error("(Fatal)Got exception!", e);
				return 1;
			}
		}
	}
	@FunctionalInterface
	private interface RoverProgramRunner {
		void doProgram(RoverReadTable read, RoverWriteTable write);
	}
	@SuppressWarnings("SameReturnValue")
	@Deprecated
	private static int connectOuthouse(OuthouseProgramOptions options) throws Exception {
		LOGGER.info("Beginning outhouse program");
		PacketCollectionIdGenerator idGenerator = createIdGenerator(options.getUniqueIdsInOneHour());
		PacketHandlerBundle packetHandlerBundle = PacketHandlerInit.getPacketHandlerBundle(getDatabaseConfigs(options), "outhouse", "outhouse_events_unused");
		List<PacketHandler> packetHandlers = new ArrayList<>(packetHandlerBundle.getStatusPacketHandlers());
		try (IOBundle ioBundle = createIOBundle(options.getIOBundleFile(), new SerialConfigBuilder(9600).build())) {
			initReader(
					ioBundle.getInputStream(),
					new OuthousePacketCreator(),
					new TimedPacketReceiver(
							10,
							new PacketListReceiverMultiplexer(
									getSourceAndFragmentUpdater(options),
									new PacketHandlerPacketListReceiver(new PacketHandlerMultiplexer(packetHandlers), idGenerator)
							),
							OnDataReceive.Defaults.NOTHING
					)
			);
		}
		return 0;
	}

	private static void initReader(InputStream in, TextPacketCreator packetCreator, RawPacketReceiver rawPacketReceiver) {
		SolarReader run = new SolarReader(in, packetCreator, rawPacketReceiver);
		try {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					run.update();
				} catch (EOFException e) {
					break;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static PacketListReceiver getSourceAndFragmentUpdater(PacketHandlingOption options){
		String source = options.getSourceId();
		Integer fragment = options.getFragmentId();
		requireNonNull(source);
		return (list, wasInstant) -> {
			list.add(InstanceSourcePackets.create(source));
			if(fragment != null){
				list.add(InstanceFragmentIndicatorPackets.create(fragment));
			}
		};
	}
	private static PacketCollectionIdGenerator createIdGenerator(Integer uniqueIdsInOneHour){
		if(uniqueIdsInOneHour == null){
			return PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR;
		}
		if(uniqueIdsInOneHour <= 0){
			throw new IllegalArgumentException("unique must be > 0 or not specified!");
		}
		return new HourIntervalPacketCollectionIdGenerator(uniqueIdsInOneHour, new Random().nextInt());
	}
	private static List<DatabaseConfig> getDatabaseConfigs(PacketHandlingOption options){
		List<File> files = options.getDatabaseConfigurationFiles();
		List<DatabaseConfig> r = new ArrayList<>();
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		mapper.getSubtypeResolver().registerSubtypes(
				DatabaseSettings.class,
				CouchDbDatabaseSettings.class,
				InfluxDbDatabaseSettings.class,
				LatestFileDatabaseSettings.class
		);
		for(File file : files){
			FileInputStream reader;
			try {
				reader = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			final DatabaseConfig databaseConfig;
			try {
				databaseConfig = mapper.readValue(reader, DatabaseConfig.class);
			} catch (IOException e) {
				throw new RuntimeException("Couldn't parse data!", e);
			}
			r.add(databaseConfig);
		}
		return r;
	}

	private static IOBundle createIOBundle(File configFile, SerialConfig defaultSerialConfig) throws Exception {
		final FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(configFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		InjectableValues.Std iv = new InjectableValues.Std();
		iv.addValue(SerialIOConfig.DEFAULT_SERIAL_CONFIG_KEY, defaultSerialConfig);

		ObjectMapper mapper = JacksonUtil.defaultMapper();
		mapper.setInjectableValues(iv);
		final IOConfig config;
		try {
			config = mapper.readValue(inputStream, IOConfig.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return config.createIOBundle();
	}

	public static int doMain(String[] args){
		LOGGER.info("[LOG] Beginning main");
		System.out.println("[stdout] Beginning main");
		if(args.length < 1){
			System.err.println("Usage: <java -jar ...> {base config file}");
			LOGGER.error("(Fatal)Incorrect args");
			return 1;
		}
		File baseConfigFile = new File(args[0]);
		final FileReader fileReader;
		try {
			fileReader = new FileReader(baseConfigFile);
		} catch(FileNotFoundException ex){
			LOGGER.error("(Fatal)File not found", ex);
			return 1;
		}
		final ProgramOptions options;
		try {
			options = MAPPER.readValue(fileReader, ProgramOptions.class);
		} catch (IOException e) {
			LOGGER.error("(Fatal)Error while parsing ProgramOptions. args=" + Arrays.toString(args), e);
			return 1;
		}
		final ProgramType programType = options.getProgramType();
		try {
			if(programType == ProgramType.MATE) {
				return connectMate((MateProgramOptions) options);
			} else if(programType == ProgramType.ROVER){
				return connectRover((RoverProgramOptions) options);
			} else if(programType == ProgramType.OUTHOUSE){
				//noinspection deprecation
				return connectOuthouse((OuthouseProgramOptions) options);
			} else if(programType == ProgramType.ROVER_SETUP){
				return connectRoverSetup((RoverSetupProgramOptions) options);
			}
			throw new AssertionError("Unknown program type... type=" + programType + " programOptions=" + options);
		} catch (Throwable t) {
			LOGGER.error("(Fatal)Got throwable", t);
			return 1;
		}
	}

	public static void main(String[] args) {
		System.exit(doMain(args));
	}
}
