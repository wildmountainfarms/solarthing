package me.retrodaredevil.solarthing;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.modbus.*;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.io.serial.SerialPortException;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.commands.sequence.CommandSequence;
import me.retrodaredevil.solarthing.config.JsonCouchDb;
import me.retrodaredevil.solarthing.config.JsonIO;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.LatestFileDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.*;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketSaver;
import me.retrodaredevil.solarthing.outhouse.OuthousePacketCreator;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.creation.PacketProvider;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.packets.handling.implementations.FileWritePacketHandler;
import me.retrodaredevil.solarthing.packets.handling.implementations.GsonStringPacketHandler;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.packets.security.crypto.DirectoryKeyMap;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.renogy.rover.*;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveWrite;
import me.retrodaredevil.util.json.JsonFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static java.util.Objects.requireNonNull;

public final class SolarMain {
	private SolarMain(){ throw new UnsupportedOperationException(); }
	
	private static final Logger LOGGER = LogManager.getLogger(SolarMain.class);
	
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
	private static final String DATABASE_UPLOAD_ID = "packet_upload";
	private static final String DATABASE_COMMAND_DOWNLOAD_ID = "command_download";
	
	private static int connectMate(MateProgramOptions options) throws Exception {
		PacketCollectionIdGenerator idGenerator = createIdGenerator(options.getUniqueIdsInOneHour());
		final IOBundle createdIO = createIOBundle(options.getIOBundleFile(), MATE_CONFIG);
		final IOBundle io;
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
		List<DatabaseConfig> databaseConfigs = getDatabaseConfigs(options);
		
		final OnDataReceive onDataReceive;
		List<PacketHandler> packetHandlers = new ArrayList<>();
		if(options.isAllowCommands()) {
			LOGGER.info("Commands are allowed");
			List<CommandProvider<MateCommand>> commandProviders = new ArrayList<>();
			{ // InputStreamCommandProvider command_input.txt block
				// TODO make the file path customizable through json (a DatabaseConfig)
				InputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(new File("command_input.txt"));
				} catch (FileNotFoundException e) {
					LOGGER.info("no command input file!");
				}
				if (fileInputStream != null) {
					commandProviders.add(InputStreamCommandProvider.createFrom(fileInputStream, "command_input.txt", EnumSet.allOf(MateCommand.class)));
				}
			}
			
			final List<PacketHandler> commandRequesterHandlerList = new ArrayList<>(); // Handlers to request and get new commands to send (This may block the current thread)
			final List<PacketHandler> commandFeedbackHandlerList = new ArrayList<>(); // Handlers to handle successful command packets, usually by storing those packets somewhere (May block the current thread)
			for(DatabaseConfig config : databaseConfigs){
				if(CouchDbDatabaseSettings.TYPE.equals(config.getType())){
					CouchDbDatabaseSettings settings = (CouchDbDatabaseSettings) config.getSettings();
					CouchProperties couchProperties = settings.getCouchProperties();
					LatestPacketHandler latestPacketHandler = new LatestPacketHandler(true);
					packetHandlers.add(latestPacketHandler);
					final CommandSequenceDataReceiver<MateCommand> commandSequenceDataReceiver;
					{
						CommandSequence<MateCommand> generatorShutOff = CommandSequences.createAuxGeneratorShutOff(latestPacketHandler::getLatestPacketCollection);
						Map<String, CommandSequence<MateCommand>> map = new HashMap<>();
						map.put("GEN OFF", generatorShutOff);
						commandSequenceDataReceiver = new CommandSequenceDataReceiver<>(map);
					}
					commandProviders.add(commandSequenceDataReceiver.getCommandProvider());
					
					IndividualSettings individualSettings = config.getIndividualSettingsOrDefault(DATABASE_COMMAND_DOWNLOAD_ID, null);
					FrequencySettings frequencySettings = individualSettings != null ? individualSettings.getFrequencySettings() : FrequencySettings.NORMAL_SETTINGS;
					commandRequesterHandlerList.add(new ThrottleFactorPacketHandler(new PrintPacketHandleExceptionWrapper(
						new CouchDbPacketRetriever(
							couchProperties,
							"commands",
							new SecurityPacketReceiver(new DirectoryKeyMap(new File("authorized")), commandSequenceDataReceiver, new DirectoryKeyMap(new File("unauthorized"))),
							true
						)
					), frequencySettings, true));
					commandFeedbackHandlerList.add(new CouchDbPacketSaver(couchProperties, "command_feedback"));
				}
			}
			
			final PacketHandler commandRequesterHandler = new PacketHandlerMultiplexer(commandRequesterHandlerList);
			final PacketHandler commandFeedbackHandler = new PacketHandlerMultiplexer(commandFeedbackHandlerList);
			Collection<MateCommand> allowedCommands = EnumSet.of(MateCommand.AUX_OFF, MateCommand.AUX_ON, MateCommand.USE, MateCommand.DROP);
			onDataReceive = new MateCommandSender(
				new CommandProviderMultiplexer<>(commandProviders),
				io.getOutputStream(),
				allowedCommands,
				new OnMateCommandSent(commandFeedbackHandler)
			);
			packetHandlers.add(commandRequesterHandler);
		} else {
			LOGGER.info("Commands are disabled");
			onDataReceive = OnDataReceive.Defaults.NOTHING;
		}
		
		packetHandlers.addAll(getPacketHandlers(databaseConfigs, "solarthing"));
		
		try {
			initReader(
				io.getInputStream(),
				new MatePacketCreator49(MateProgramOptions.getIgnoreCheckSum(options)),
				new PacketHandlerMultiplexer(packetHandlers),
				idGenerator,
				250,
				onDataReceive,
				getAdditionalPacketProvider(options)
			);
		} finally {
			io.close();
		}
		return 0;
	}
	private static int connectRover(RoverProgramOptions options){
		List<PacketHandler> packetHandlers = getPacketHandlers(getDatabaseConfigs(options), "solarthing");
		PacketHandler packetHandler = new PacketHandlerMultiplexer(packetHandlers);
		PacketProvider packetProvider = getAdditionalPacketProvider(options);
		
		PacketCollectionIdGenerator idGenerator = createIdGenerator(options.getUniqueIdsInOneHour());
		try(IOBundle ioBundle = createIOBundle(options.getIOBundleFile(), ROVER_CONFIG)) {
			ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RTUDataEncoder(300, 10));
			ModbusSlave slave = new ImmutableAddressModbusSlave(options.getModbusAddress(), modbus);
			RoverReadTable read = new RoverModbusSlaveRead(slave);
			try {
				while (!Thread.currentThread().isInterrupted()) {
					final long startTime = System.currentTimeMillis();
					final RoverStatusPacket packet;
					try {
						packet = RoverStatusPackets.createFromReadTable(read);
					} catch(ModbusRuntimeException e){
						LOGGER.error("Modbus exception", e);
						Thread.sleep(1000);
						continue;
					}
					LOGGER.debug(
						JsonFile.gson.toJson(packet) + "\n" +
						packet.getSpecialPowerControlE021().getFormattedInfo().replaceAll("\n", "\n\t") + "\n" +
						packet.getSpecialPowerControlE02D().getFormattedInfo().replaceAll("\n", "\n\t")
					);
					List<Packet> packets = new ArrayList<>();
					packets.add(packet);
					packets.addAll(packetProvider.createPackets());
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
					Thread.sleep(Math.max(200, 5000 - readDuration)); // allow 5 seconds to read from rover // assume saveDuration is very small
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} catch (Exception e) {
			LOGGER.error("Unable to connect to rover", e);
			return 1;
		}
		return 0;
	}
	private static int connectRoverSetup(RoverSetupProgramOptions options) throws IOException{
		File dummyFile = options.getDummyFile();
		if(dummyFile != null){
			final byte[] bytes = Files.readAllBytes(dummyFile.toPath());
			String json = new String(bytes, StandardCharsets.UTF_8);
			JsonObject jsonPacket = new GsonBuilder().create().fromJson(json, JsonObject.class);
			RoverStatusPacket roverStatusPacket = RoverStatusPackets.createFromJson(jsonPacket);
			DummyRoverReadWrite readWrite = new DummyRoverReadWrite(
				roverStatusPacket,
				(fieldName, previousValue, newValue) -> System.out.println(fieldName + " changed from " + previousValue + " to " + newValue)
			);
			RoverSetupProgram.startRoverSetup(readWrite, readWrite);
		} else {
			try(IOBundle ioBundle = createIOBundle(options.getIOBundleFile(), ROVER_CONFIG)) {
				ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RTUDataEncoder(300, 10));
				ModbusSlave slave = new ImmutableAddressModbusSlave(options.getModbusAddress(), modbus);
				RoverReadTable read = new RoverModbusSlaveRead(slave);
				RoverWriteTable write = new RoverModbusSlaveWrite(slave);
				RoverSetupProgram.startRoverSetup(read, write);
			} catch (Exception e) {
				LOGGER.error("Got exception!", e);
				return 1;
			}
		}
		return 0;
	}
	private static int connectOuthouse(OuthouseProgramOptions options) throws Exception {
		PacketCollectionIdGenerator idGenerator = createIdGenerator(options.getUniqueIdsInOneHour());
		List<PacketHandler> packetHandlers = getPacketHandlers(getDatabaseConfigs(options), "outhouse");
		try (IOBundle ioBundle = createIOBundle(options.getIOBundleFile(), new SerialConfigBuilder(9600).build())) {
			initReader(
				ioBundle.getInputStream(),
				new OuthousePacketCreator(),
				new PacketHandlerMultiplexer(packetHandlers),
				idGenerator,
				10,
				OnDataReceive.Defaults.NOTHING,
				getAdditionalPacketProvider(options)
			);
		}
		return 0;
	}
	
	private static void initReader(InputStream in, TextPacketCreator packetCreator, PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, long samePacketTime, OnDataReceive onDataReceive, PacketProvider additionalPacketProvider) {
		Runnable run = new SolarReader(in, packetCreator, packetHandler, idGenerator, samePacketTime, onDataReceive, additionalPacketProvider);
		try {
			while (!Thread.currentThread().isInterrupted()) {
				run.run();
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	private static PacketProvider getAdditionalPacketProvider(PacketHandlingOption options){
		String source = options.getSourceId();
		Integer fragment = options.getFragmentId();
		requireNonNull(source);
		return () -> {
			List<Packet> r = new ArrayList<>();
			r.add(InstanceSourcePackets.create(source));
			if(fragment != null){
				r.add(InstanceFragmentIndicatorPackets.create(fragment));
			}
			return r;
		};
	}
	private static PacketCollectionIdGenerator createIdGenerator(Integer uniqueIdsInOneHour){
		if(uniqueIdsInOneHour == null){
			return PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR;
		}
		if(uniqueIdsInOneHour <= 0){
			throw new IllegalArgumentException("--unique must be > 0 or not specified!");
		}
		return new HourIntervalPacketCollectionIdGenerator(uniqueIdsInOneHour, new Random().nextInt());
	}
	private static List<PacketHandler> getPacketHandlers(List<DatabaseConfig> configs, String couchDbDatabaseName){
		List<PacketHandler> r = new ArrayList<>();
		for(DatabaseConfig config : configs) {
			if (CouchDbDatabaseSettings.TYPE.equals(config.getType())) {
				CouchDbDatabaseSettings settings = (CouchDbDatabaseSettings) config.getSettings();
				CouchProperties couchProperties = settings.getCouchProperties();
				IndividualSettings individualSettings = config.getIndividualSettingsOrDefault(DATABASE_UPLOAD_ID, null);
				FrequencySettings frequencySettings = individualSettings != null ? individualSettings.getFrequencySettings() : FrequencySettings.NORMAL_SETTINGS;
				r.add(new ThrottleFactorPacketHandler(
					new PrintPacketHandleExceptionWrapper(new CouchDbPacketSaver(couchProperties, couchDbDatabaseName)),
					frequencySettings,
					true
				));
			} else if (LatestFileDatabaseSettings.TYPE.equals(config.getType())){
				LatestFileDatabaseSettings settings = (LatestFileDatabaseSettings) config.getSettings();
				r.add(new FileWritePacketHandler(settings.getFile(), new GsonStringPacketHandler(), false));
			}
		}
		return r;
	}
	private static List<DatabaseConfig> getDatabaseConfigs(PacketHandlingOption options){
		List<File> files = options.getDatabaseConfigurationFiles();
		List<DatabaseConfig> r = new ArrayList<>();
		JsonParser parser = new JsonParser();
		for(File file : files){
			final String contents;
			try {
				contents = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			JsonObject jsonObject = parser.parse(contents).getAsJsonObject();
			String type = jsonObject.getAsJsonPrimitive("type").getAsString();
			JsonElement configElement = jsonObject.get("config");
			final DatabaseType databaseType;
			final DatabaseSettings databaseSettings;
			if ("couchdb".equals(type)) {
				databaseType = CouchDbDatabaseSettings.TYPE;
				JsonObject config = configElement.getAsJsonObject();
				CouchProperties couchProperties = JsonCouchDb.getCouchPropertiesFromJson(config);
				databaseSettings = new CouchDbDatabaseSettings(couchProperties);
			} else if ("latest".equals(type)) {
				databaseType = LatestFileDatabaseSettings.TYPE;
				JsonObject config = configElement.getAsJsonObject();
				String path = config.get("file").getAsString();
				File latestFile = new File(path).getAbsoluteFile();
				databaseSettings = new LatestFileDatabaseSettings(latestFile);
			} else {
				throw new UnsupportedOperationException("Unknown type: " + type);
			}
			Map<String, IndividualSettings> individualSettingsMap = parseAllIndividualSettings(jsonObject.get("settings"));
			r.add(new DatabaseConfig(databaseType, databaseSettings, individualSettingsMap));
		}
		return r;
	}
	private static Map<String, IndividualSettings> parseAllIndividualSettings(JsonElement settingsElement){
		if(settingsElement == null){
			return Collections.emptyMap();
		}
		JsonObject jsonObject = settingsElement.getAsJsonObject();
		Map<String, IndividualSettings> r = new HashMap<>();
		for(Map.Entry<String, JsonElement> entrySet : jsonObject.entrySet()){
			r.put(entrySet.getKey(), parseIndividualSettings(entrySet.getValue().getAsJsonObject()));
		}
		return r;
	}
	private static IndividualSettings parseIndividualSettings(JsonObject jsonObject){
		JsonElement throttleFactorElement = jsonObject.get("throttle_factor");
		JsonElement initialSkipElement = jsonObject.get("initial_skip");
		int throttleFactor = throttleFactorElement != null ? throttleFactorElement.getAsInt() : 1;
		int initialSkip = initialSkipElement != null ? initialSkipElement.getAsInt() : 0;
		return new IndividualSettings(new FrequencySettings(throttleFactor, initialSkip));
	}
	
	private static IOBundle createIOBundle(File configFile, SerialConfig defaultSerialConfig){
		final String contents;
		try {
			contents = new String(Files.readAllBytes(configFile.toPath()), Charset.defaultCharset());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(contents).getAsJsonObject();
		return createIOBundle(jsonObject, defaultSerialConfig);
	}
	public static IOBundle createIOBundle(JsonObject jsonObject, SerialConfig defaultSerialConfig){
		String type = jsonObject.get("type").getAsString();
		if("serial".equals(type)){
			try {
				return JsonIO.getSerialIOBundleFromJson(jsonObject, defaultSerialConfig);
			} catch (SerialPortException e) {
				throw new RuntimeException(e);
			}
		} else if("standard".equals(type)){
			return IOBundle.Defaults.STANDARD_IN_OUT;
		}
		throw new UnsupportedOperationException("Unknown type: " + type);
	}
	
	public static int doMain(String[] args){
		if(args.length < 1){
			System.err.println("Usage: <java -jar ...> {mate|rover|rover-setup|outhouse}");
			return 1;
		}
		String programName = args[0];
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		
		
		Program program = getProgram(programName);
		if(program == null){
			System.err.println("Usage: <java -jar ...> {mate|rover|rover-setup|outhouse}");
			return 1;
		}
		try {
			if(program == Program.MATE) {
				Cli<MateProgramOptions> cli = CliFactory.createCli(MateProgramOptions.class);
				final MateProgramOptions options;
				try {
					options = cli.parseArguments(newArgs);
				} catch(ArgumentValidationException ex){
					System.err.println(ex.getMessage());
					return 1;
				}
				return connectMate(options);
			} else if(program == Program.ROVER){
				Cli<RoverProgramOptions> cli = CliFactory.createCli(RoverProgramOptions.class);
				final RoverProgramOptions options;
				try {
					options = cli.parseArguments(newArgs);
				} catch(ArgumentValidationException ex){
					System.err.println(ex.getMessage());
					return 1;
				}
				return connectRover(options);
			} else if(program == Program.OUTHOUSE){
				Cli<OuthouseProgramOptions> cli = CliFactory.createCli(OuthouseProgramOptions.class);
				final OuthouseProgramOptions options;
				try {
					options = cli.parseArguments(newArgs);
				} catch(ArgumentValidationException ex){
					System.err.println(ex.getMessage());
					return 1;
				}
				return connectOuthouse(options);
			} else if(program == Program.ROVER_SETUP){
				Cli<RoverSetupProgramOptions> cli = CliFactory.createCli(RoverSetupProgramOptions.class);
				final RoverSetupProgramOptions options;
				try {
					options = cli.parseArguments(newArgs);
				} catch(ArgumentValidationException ex){
					System.err.println(ex.getMessage());
					return 1;
				}
				return connectRoverSetup(options);
			}
			System.out.println("Specify mate|rover|rover-setup|outhouse");
			return 1;
		} catch (Exception t) {
			t.printStackTrace();
			return 1;
		}
	}

	public static void main(String[] args) {
		System.exit(doMain(args));
	}
	private static Program getProgram(String program) {
		if(program == null){
			return null;
		}
		switch (program.toLowerCase()) {
			case "mate":
				return Program.MATE;
			case "rover":
				return Program.ROVER;
			case "rover-setup":
				return Program.ROVER_SETUP;
			case "outhouse":
				return Program.OUTHOUSE;
		}
		return null;
	}
	private enum Program {
		MATE,
		ROVER,
		ROVER_SETUP,
		OUTHOUSE
	}
}
