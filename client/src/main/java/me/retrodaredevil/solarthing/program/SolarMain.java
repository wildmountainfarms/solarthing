package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.LatestFileDatabaseSettings;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.io.SerialIOConfig;
import me.retrodaredevil.solarthing.config.options.*;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.RawPacketReceiver;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.program.pvoutput.PVOutputUploadMain;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.util.Objects.requireNonNull;

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
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();
	static {
		MAPPER.getSubtypeResolver().registerSubtypes(
				DatabaseSettings.class,
				CouchDbDatabaseSettings.class,
				InfluxDbDatabaseSettings.class,
				LatestFileDatabaseSettings.class
		);
	}

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
		return (list, instantType) -> {
			list.add(InstanceSourcePackets.create(source));
			list.add(InstanceFragmentIndicatorPackets.create(fragment));
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

	public static IOBundle createIOBundle(File configFile, SerialConfig defaultSerialConfig) {
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
		try {
			return config.createIOBundle();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static CouchDbQueryHandler createCouchDbQueryHandler(DatabaseOption options) {
		DatabaseConfig databaseConfig = SolarMain.getDatabaseConfig(options.getDatabase());
		DatabaseType databaseType = databaseConfig.getType();
		if(databaseType != CouchDbDatabaseSettings.TYPE){
			throw new IllegalArgumentException("Only CouchDb can be used for this program type right now!");
		}
		CouchDbDatabaseSettings couchDbDatabaseSettings = (CouchDbDatabaseSettings) databaseConfig.getSettings();
		CouchProperties couchProperties = couchDbDatabaseSettings.getCouchProperties();
		final HttpClient httpClient = EktorpUtil.createHttpClient(couchProperties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		return new CouchDbQueryHandler(new StdCouchDbConnector(SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, instance));
	}

	public static int doMain(String[] args){
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "[LOG] Beginning main. Jar: " + JarUtil.getJarFileName());
		System.out.println("[stdout] Beginning main");
		if(args.length < 1){
			System.err.println("Usage: <java -jar ...> {base config file}");
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Incorrect args");
			return 1;
		}
		File baseConfigFile = new File(args[0]);
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
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Error while parsing ProgramOptions. args=" + Arrays.toString(args), e);
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
				return PVOutputUploadMain.startPVOutputUpload((PVOutputUploadProgramOptions) options, Arrays.copyOfRange(args, 1, args.length), dataDirectory);
			} else if(programType == ProgramType.MESSAGE_SENDER){
				return MessageSenderMain.startMessageSender((MessageSenderProgramOptions) options);
			} else if(programType == ProgramType.REQUEST) {
				return RequestMain.startRequestProgram((RequestProgramOptions) options, dataDirectory);
			}
			throw new AssertionError("Unknown program type... type=" + programType + " programOptions=" + options);
		} catch (Throwable t) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Got throwable", t);
			return 1;
		}
	}

	public static void main(String[] args) {
		System.exit(doMain(args));
	}
}
