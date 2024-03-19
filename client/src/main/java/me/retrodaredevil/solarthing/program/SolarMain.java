package me.retrodaredevil.solarthing.program;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import me.retrodaredevil.couchdbjava.exception.CouchDbCodeException;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.ConfigException;
import me.retrodaredevil.solarthing.config.ConfigUtil;
import me.retrodaredevil.solarthing.config.databases.DatabaseConfig;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.*;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.RawPacketReceiver;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.program.action.RunActionMain;
import me.retrodaredevil.solarthing.program.check.CheckMain;
import me.retrodaredevil.solarthing.program.couchdb.CouchDbSetupMain;
import me.retrodaredevil.solarthing.program.pvoutput.PVOutputUploadMain;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
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

	public static int initReader(InputStream in, Runnable reloadIO, TextPacketCreator packetCreator, RawPacketReceiver rawPacketReceiver) {
		SolarReader solarReader = new SolarReader(in, packetCreator, rawPacketReceiver);
		try {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					solarReader.update();
				} catch (EOFException e) {
					return 0;
				} catch (IOException e) {
					LOGGER.error("Got IOException!", e);
					Thread.sleep(500);
					reloadIO.run();
					LOGGER.debug("Reloaded IO bundle");
					Thread.sleep(1000);
				}
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return SolarThingConstants.EXIT_CODE_INTERRUPTED;
	}

	public static PacketListReceiver getSourceAndFragmentUpdater(PacketHandlingOption options){
		String source = options.getSourceId();
		int fragment = options.getFragmentId();
		requireNonNull(source);
		Packet sourcePacket = InstanceSourcePackets.create(source);
		Packet fragmentPacket = InstanceFragmentIndicatorPackets.create(fragment);
		return (list) -> {
			list.add(sourcePacket);
			list.add(fragmentPacket);
		};
	}
	public static PacketCollectionIdGenerator createIdGenerator(Integer uniqueIdsInOneHour, boolean shortDocumentId){
		if(uniqueIdsInOneHour == null){
			return PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR;
		}
		return new HourIntervalPacketCollectionIdGenerator(uniqueIdsInOneHour, new Random().nextInt(), shortDocumentId);
	}

	private static String getJarInfo() {
		JarUtil.Data data = JarUtil.getData();
		Instant lastModified = data.getLastModifiedInstantOrNull();
		return "Jar: " + JarUtil.getJarFileName()
				+ " Last Modified: " + (lastModified == null ? "unknown" : lastModified.toString())
				+ " Java version: " + System.getProperty("java.version");
	}

	public static int doMainCommand(CommandOptions commandOptions, Path baseConfigFile) {
		String user = System.getProperty("user.name");
		if (!"solarthing".equals(user) && !SolarThingEnvironment.isRunningInDocker()) {
			if (user.equals("root")) {
				LOGGER.warn("Running as root user!");
				System.out.println("\n\nHey! We noticed you are running as root! Instead of\n  sudo ./run.sh\nPlease do\n  sudo -u solarthing ./run.sh\n instead.\n");
			} else {
				LOGGER.info("Running as " + user);
			}
		}

		LOGGER.info("Using base configuration file: " + baseConfigFile);
		if (Files.notExists(baseConfigFile)) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Base configuration file does not exist!");
			return SolarThingConstants.EXIT_CODE_INVALID_CONFIG;
		}
		final ProgramOptions options;
		try {
			options = ConfigUtil.readConfig(baseConfigFile, ProgramOptions.class);
		} catch (ConfigException e) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Error while parsing ProgramOptions.", e);
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Hey! The error you got above might be scary, but this message might be helpful:\n\n" + e.getMessage());
			return SolarThingConstants.EXIT_CODE_INVALID_CONFIG;
		}

		// Note we used to have the creation of the .data directory here. We may consider adding it back in the future should we need it

		final ProgramType programType = options.getProgramType();
		try {
			if(programType == ProgramType.MATE) {
				return OutbackMateMain.connectMate((MateProgramOptions) options, commandOptions.isValidate());
			} else if(programType == ProgramType.ROVER_SETUP){
				return RoverMain.connectRoverSetup((RoverSetupProgramOptions) options, commandOptions.isValidate());
			} else if(programType == ProgramType.PVOUTPUT_UPLOAD){
				return PVOutputUploadMain.startPVOutputUpload((PVOutputUploadProgramOptions) options, commandOptions, commandOptions.isValidate());
			} else if(programType == ProgramType.REQUEST) {
				return RequestMain.startRequestProgram((RequestProgramOptions) options, commandOptions.isValidate());
			} else if(programType == ProgramType.AUTOMATION) {
				return AutomationMain.startAutomation((AutomationProgramOptions) options, commandOptions.isValidate());
			}
			throw new AssertionError("Unknown program type... type=" + programType + " programOptions=" + options);
		} catch (ConfigException e) {
			String logMessage = "Ending SolarThing. " + getJarInfo();
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "[LOG] " + logMessage);
			System.out.println("[stdout] " + logMessage);
			System.err.println("[stderr] " + logMessage);
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Got config exception", e);
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "SolarThing is shutting down. This is caused by an error in your configuration or in how your environment is set up. Detailed message below:\n\n" + e.getUserMessage());
			LogManager.shutdown();
			return SolarThingConstants.EXIT_CODE_INVALID_CONFIG;
		} catch (Throwable t) {
			boolean invalidJar = t instanceof ClassNotFoundException || t instanceof NoClassDefFoundError;
			if (invalidJar) {
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "We're about to give you an error with some technical stuff, but this error is likely caused by you switching out jar files while SolarThing is running. If it isn't, please report this error.");
			}
			boolean uncommonError = t instanceof UnsatisfiedLinkError;
			if (uncommonError) {
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Got an UnsatisfiedLinkError which is uncommon. If setup correctly, after this crash program will relaunch (hopefully successfully).");
			}
			String logMessage = "Ending SolarThing. " + getJarInfo();
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "[LOG] " + logMessage);
			System.out.println("[stdout] " + logMessage);
			System.err.println("[stderr] " + logMessage);
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Got throwable", t);
			LOGGER.debug("Going to shutdown LogManager.");
			LogManager.shutdown(); // makes sure all buffered logs are flushed // this should be done automatically, but we'll do it anyway
			System.err.println();
			t.printStackTrace(System.err); // print to stderr just in case logging isn't going well
			if (invalidJar) {
				return SolarThingConstants.EXIT_CODE_RESTART_NEEDED_JAR_UPDATED;
			}
			if (uncommonError) {
				return SolarThingConstants.EXIT_CODE_RESTART_NEEDED_UNCOMMON_ERROR;
			}
			return SolarThingConstants.EXIT_CODE_CRASH;
		}
	}

	private static int doMain(String[] args){
		String logMessage = "Beginning main. " + getJarInfo();
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
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		if (commandOptions.getBaseConfigFile() != null) {
			return doMainCommand(commandOptions, Path.of(commandOptions.getBaseConfigFile()));
		}
		if (commandOptions.getCouchDbSetupFile() != null) {
			final DatabaseConfig config;
			try {
				config = ConfigUtil.readConfig(Path.of(commandOptions.getCouchDbSetupFile()), DatabaseConfig.class);
			} catch (ConfigException e) {
				e.printStackTrace();
				System.err.println("Problem reading CouchDB database settings file.");
				return SolarThingConstants.EXIT_CODE_INVALID_CONFIG;
			}
			DatabaseSettings settings = config.requireDatabaseSettings();
			if (!(settings instanceof CouchDbDatabaseSettings)) {
				System.err.println("Must be CouchDB database settings!");
				return SolarThingConstants.EXIT_CODE_INVALID_CONFIG;
			}
			try {
				return CouchDbSetupMain.createFrom((CouchDbDatabaseSettings) settings).doCouchDbSetupMain();
			} catch (CouchDbException e) {
				if (e instanceof CouchDbCodeException) {
					ErrorResponse error = ((CouchDbCodeException) e).getErrorResponse();
					if (error != null) {
						System.err.println(error.getError());
						System.err.println(error.getReason());
					}
				}
				throw new RuntimeException(e);
			}
		}
		List<String> legacyArguments = commandOptions.getLegacyOptionsRaw();
		if (legacyArguments == null || legacyArguments.isEmpty()) {
			System.err.println(cli.getHelpMessage());
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		System.err.println("Invalid sub command: " + legacyArguments.get(0));
		return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
	}
	private static int outputVersion() {
		JarUtil.Data data = JarUtil.getData();
		Instant lastModified = data.getLastModifiedInstantOrNull();
		String commitHash = SolarThingEnvironment.getGitCommitHash();
		String ref = SolarThingEnvironment.getRef();
		System.out.println("SolarThing made by Lavender Shannon\n" +
				"Jar: " + data.getJarFileNameOrNull() + "\n" +
				"Jar last modified: " + (lastModified == null ? "unknown" : lastModified.toString()) + "\n" +
				(commitHash == null ? "" : "Commit hash: " + commitHash + "\n") +
				(ref == null ? "" : "Ref: " + ref + "\n") +
				"Java version: " + System.getProperty("java.version"));
		return 0;
	}
	private static int determineMainSubprogram(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: solarthing [command] [args]\n\n" +
					"Commands:\n" +
					"  run [options]\n" +
					"  version\n" +
					"  check --port <serial port> [--type <type>]\n" +
					"  action [file]");
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		String firstArg = args[0];
		String[] subArgs = new String[args.length - 1];
		System.arraycopy(args, 1, subArgs, 0, args.length - 1);
		switch (firstArg) {
			case "run":
				return doMain(subArgs);
			case "version":
				return outputVersion();
			case "action":
				return RunActionMain.runAction(subArgs);
			case "check":
				return CheckMain.doCheck(subArgs);
			default:
				System.err.println("Unknown argument: " + firstArg + "\n" +
						"Previous SolarThing versions allowed the running of SolarThing without the 'run' prefix.\n" +
						"Adding 'run' may fix your problem.");
				return 1;
		}
	}

	public static void main(String[] args) {
		System.exit(determineMainSubprogram(args));
	}
}
