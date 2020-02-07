package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.LatestFileDatabaseSettings;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.io.SerialIOConfig;
import me.retrodaredevil.solarthing.config.options.*;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

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
		Integer fragment = options.getFragmentId();
		requireNonNull(source);
		return (list, wasInstant) -> {
			list.add(InstanceSourcePackets.create(source));
			if(fragment != null){
				list.add(InstanceFragmentIndicatorPackets.create(fragment));
			}
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

	public static IOBundle createIOBundle(File configFile, SerialConfig defaultSerialConfig) throws Exception {
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
		File dataDirectory = new File(".data");
		if(!dataDirectory.mkdirs() && !dataDirectory.isDirectory()){
			LOGGER.error("(Fatal)Unable to create data directory! dataDirectory=" + dataDirectory + " absolute=" + dataDirectory.getAbsolutePath());
			return 1;
		}
		final ProgramType programType = options.getProgramType();
		try {
			if(programType == ProgramType.MATE) {
				return OutbackMateMain.connectMate((MateProgramOptions) options, dataDirectory);
			} else if(programType == ProgramType.ROVER){
				return RoverMain.connectRover((RoverProgramOptions) options);
			} else if(programType == ProgramType.ROVER_SETUP){
				return RoverMain.connectRoverSetup((RoverSetupProgramOptions) options);
			} else if(programType == ProgramType.PVOUTPUT_UPLOAD){
				return PVOutputUploadMain.startPVOutputUpload((PVOutputUploadProgramOptions) options);
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
