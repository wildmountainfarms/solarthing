package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettingsUtil;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.*;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.io.SerialIOConfig;
import me.retrodaredevil.solarthing.config.options.*;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.RawPacketReceiver;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.program.pvoutput.PVOutputUploadMain;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.apache.logging.log4j.LogManager;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class SolarMain {
	private SolarMain(){ throw new UnsupportedOperationException(); }

	/*
	So you think this class is complicated? Well I'll have to agree with you on that. This is probably the most
	complicated class in this project. However, that's a good thing. By having most of the complexity in this class,
	we have less complexity in other classes. Most configuration of how the actual program works goes on in here.
	I'm sure there's a better way to write this class. However the main advantage of it right now is that
	it's pretty easy to change behaviour, and that's what's important.
	 */

	private static final Logger LOGGER = LoggerFactory.getLogger(SolarMain.class);
	private static final ObjectMapper MAPPER = DatabaseSettingsUtil.registerDatabaseSettings(JacksonUtil.defaultMapper());

	public static void initReader(InputStream in, TextPacketCreator packetCreator, RawPacketReceiver rawPacketReceiver) {
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

	public static PacketListReceiver getSourceAndFragmentUpdater(PacketHandlingOption options){
		String source = options.getSourceId();
		int fragment = options.getFragmentId();
		requireNonNull(source);
		Packet sourcePacket = InstanceSourcePackets.create(source);
		Packet fragmentPacket = InstanceFragmentIndicatorPackets.create(fragment);
		return (list, instantType) -> {
			list.add(sourcePacket);
			list.add(fragmentPacket);
		};
	}
	public static PacketCollectionIdGenerator createIdGenerator(Integer uniqueIdsInOneHour){
		if(uniqueIdsInOneHour == null){
			return PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR;
		}
		if(uniqueIdsInOneHour <= 0){
			throw new IllegalArgumentException("unique must be > 0 or not specified!");
		}
		return new HourIntervalPacketCollectionIdGenerator(uniqueIdsInOneHour, new Random().nextInt());
	}
	public static List<DatabaseConfig> getDatabaseConfigs(PacketHandlingOption options){
		List<File> files = options.getDatabaseConfigurationFiles();
		List<DatabaseConfig> r = new ArrayList<>();
		for(File file : files){
			r.add(getDatabaseConfig(file));
		}
		return r;
	}
	public static DatabaseConfig getDatabaseConfig(File file){
		FileInputStream reader;
		try {
			reader = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			return MAPPER.readValue(reader, DatabaseConfig.class);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't parse data!", e);
		}
	}

	public static IOConfig parseIOConfig(File configFile, SerialConfig defaultSerialConfig) {
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
		return config;
	}
	public static IOBundle createIOBundle(File configFile, SerialConfig defaultSerialConfig) {
		IOConfig config = parseIOConfig(configFile, defaultSerialConfig);
		try {
			return config.createIOBundle();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static CouchProperties createCouchProperties(DatabaseOption options) {
		DatabaseConfig databaseConfig = SolarMain.getDatabaseConfig(options.getDatabase());
		DatabaseType databaseType = databaseConfig.getType();
		if(databaseType != CouchDbDatabaseSettings.TYPE){
			throw new IllegalArgumentException("Only CouchDb can be used for this program type right now!");
		}
		CouchDbDatabaseSettings couchDbDatabaseSettings = (CouchDbDatabaseSettings) databaseConfig.getSettings();
		return couchDbDatabaseSettings.getCouchProperties();
	}
	public static CouchDbQueryHandler createCouchDbQueryHandler(DatabaseOption options) {
		return createCouchDbQueryHandler(createCouchProperties(options));
	}
	public static CouchDbQueryHandler createCouchDbQueryHandler(CouchProperties couchProperties) {
		final HttpClient httpClient = EktorpUtil.createHttpClient(couchProperties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		return new CouchDbQueryHandler(new StdCouchDbConnector(SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, instance));
	}

	public static int doMainCommand(CommandOptions commandOptions, File baseConfigFile) {
		LOGGER.info("Using base configuration file: " + baseConfigFile);
		final FileReader fileReader;
		try {
			fileReader = new FileReader(baseConfigFile);
		} catch(FileNotFoundException ex){
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)File not found", ex);
			return 1;
		}
		final ProgramOptions options;
		try {
			options = MAPPER.readValue(fileReader, ProgramOptions.class);
		} catch (IOException e) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Error while parsing ProgramOptions.", e);
			if (e instanceof JsonParseException) {
				LOGGER.error("Hey! Just wanted to let you know that the above error is just saying that your JSON is formatted incorrectly!");
			}
			return 1;
		}
		File dataDirectory = new File(".data");
		if(!dataDirectory.mkdirs() && !dataDirectory.isDirectory()){
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Unable to create data directory! dataDirectory=" + dataDirectory + " absolute=" + dataDirectory.getAbsolutePath());
			return 1;
		}
		final ProgramType programType = options.getProgramType();
		try {
			if(programType == ProgramType.MATE) {
				return OutbackMateMain.connectMate((MateProgramOptions) options, dataDirectory);
			} else if(programType == ProgramType.ROVER){
				return RoverMain.connectRover((RoverProgramOptions) options, dataDirectory);
			} else if(programType == ProgramType.ROVER_SETUP){
				return RoverMain.connectRoverSetup((RoverSetupProgramOptions) options);
			} else if(programType == ProgramType.PVOUTPUT_UPLOAD){
				return PVOutputUploadMain.startPVOutputUpload((PVOutputUploadProgramOptions) options, commandOptions, dataDirectory);
			} else if(programType == ProgramType.REQUEST) {
				return RequestMain.startRequestProgram((RequestProgramOptions) options, dataDirectory);
			} else if(programType == ProgramType.AUTOMATION) {
				return AutomationMain.startAutomation((AutomationProgramOptions) options);
			}
			throw new AssertionError("Unknown program type... type=" + programType + " programOptions=" + options);
		} catch (Throwable t) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Got throwable", t);
			LOGGER.debug("Going to shutdown LogManager.");
			LogManager.shutdown(); // makes sure all buffered logs are flushed // this should be done automatically, but we'll do it anyway
			System.err.println();
			t.printStackTrace(System.err); // print to stderr just in case logging isn't going well
			return 1;
		}
	}

	public static int doMain(String[] args){
		String logMessage = "Beginning main. Jar: " + JarUtil.getJarFileName() + " Java version: " + System.getProperty("java.version");
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "[LOG] " + logMessage);
		System.out.println("[stdout] " + logMessage);
		System.err.println("[stderr] " + logMessage);
		Cli<CommandOptions> cli = CliFactory.createCli(CommandOptions.class);
		final CommandOptions commandOptions;
		try {
			commandOptions = cli.parseArguments(args);
		} catch (ArgumentValidationException ex) {
			System.out.println(cli.getHelpMessage());
			if (ex instanceof HelpRequestedException) {
				return 0;
			}
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, ex.getMessage());
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Incorrect args");
			return 1;
		}
		if (commandOptions.getBaseConfigFile() != null) {
			return doMainCommand(commandOptions, commandOptions.getBaseConfigFile());
		}
		if (commandOptions.getCouchDbSetupFile() != null) {
			final DatabaseConfig config;
			try {
				config = MAPPER.readValue(commandOptions.getCouchDbSetupFile(), DatabaseConfig.class);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Problem reading CouchDB database settings file.");
				return 1;
			}
			DatabaseSettings settings = config.getSettings();
			if (!(settings instanceof CouchDbDatabaseSettings)) {
				System.err.println("Must be CouchDB database settings!");
				return 1;
			}
			return CouchDbSetupMain.doCouchDbSetupMain((CouchDbDatabaseSettings) settings);
		}
		List<String> legacyArguments = commandOptions.getLegacyOptions();
		if (legacyArguments.isEmpty()) {
			System.err.println(cli.getHelpMessage());
			return 1;
		}
		System.out.println("Using legacy arguments! Please use --base instead! (If you are running this using ./run.sh, this will be automatically fixed in a future update) (ignore this).");
		return doMainCommand(commandOptions, new File(legacyArguments.get(0)));
	}

	public static void main(String[] args) {
		System.exit(doMain(args));
	}
}
