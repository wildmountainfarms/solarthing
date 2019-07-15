package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketSaver;
import me.retrodaredevil.solarthing.io.IOBundle;
import me.retrodaredevil.solarthing.io.JSerialIOBundle;
import me.retrodaredevil.solarthing.io.SerialPortException;
import me.retrodaredevil.solarthing.outhouse.OuthousePacketCreator;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.creation.PacketCreator;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.packets.handling.implementations.FileWritePacketHandler;
import me.retrodaredevil.solarthing.packets.handling.implementations.GsonStringPacketHandler;
import me.retrodaredevil.solarthing.packets.security.crypto.DirectoryKeyMap;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.commands.sequence.CommandSequence;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class SolarMain {
	/*
	TODO This solar command part of this file is not general at all. The command stuff was made for my specific use case and because of that, it would
	be hard for someone else to find use of this program without customizing it to their own needs.
	
	This is one thing we have to think about because while I want it to be easy for my own needs, I also want it to
	be customizable for others as well
	 */
	private int connectSolar(ProgramArgs args, PacketCollectionIdGenerator idGenerator) {
		final InputStream in;
		final OutputStream output;
		if(args.isUnitTest()){
			in = System.in;
			output = System.out;
		} else {
			final IOBundle port;
			try {
				port = JSerialIOBundle.createPort(args.getPortName());
			} catch (SerialPortException e) {
				e.printStackTrace();
				return 1;
			}
			in = port.getInputStream();
			output = port.getOutputStream();
		}
		LatestPacketHandler latestPacketHandler = new LatestPacketHandler(true);
		List<CommandProvider<MateCommand>> commandProviders = new ArrayList<>();
		{
			InputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(new File("command_input.txt"));
			} catch (FileNotFoundException e) {
				System.out.println("no command input file!");
			}
			if (fileInputStream != null) {
				commandProviders.add(InputStreamCommandProvider.createFrom(fileInputStream, "command_input.txt", EnumSet.allOf(MateCommand.class)));
			}
		}
		/*{
			InputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(new File("command_sequence_input.txt"));
			} catch (FileNotFoundException e) {
				System.out.println("no command sequence input file!");
			}
			if(fileInputStream != null) {
				commandProviders.add(new CommandSequenceCommandProvider(new InputStreamCommandSequenceProvider(fileInputStream, map)));
			}
		}*/
		
		
		final PacketHandler commandRequesterHandler;
		final PacketHandler commandFeedbackHandler;
		if(args.isLocal()){
			commandRequesterHandler = PacketHandler.Defaults.HANDLE_NOTHING;
			commandFeedbackHandler = PacketHandler.Defaults.HANDLE_NOTHING;
		} else {
			final CommandSequenceDataReceiver<MateCommand> commandSequenceDataReceiver;
			{
				CommandSequence<MateCommand> generatorShutOff = CommandSequences.createAuxGeneratorShutOff(latestPacketHandler::getLatestPacketCollection);
				Map<String, CommandSequence<MateCommand>> map = new HashMap<>();
				map.put("GEN OFF", generatorShutOff);
				commandSequenceDataReceiver = new CommandSequenceDataReceiver<>(map);
			}
			commandProviders.add(commandSequenceDataReceiver.getCommandProvider());
			
			commandRequesterHandler = new ThrottleFactorPacketHandler(new PrintPacketHandleExceptionWrapper(
				new CouchDbPacketRetriever(
					args.createProperties(),
					"commands",
					new SecurityPacketReceiver(new DirectoryKeyMap(new File("authorized")), commandSequenceDataReceiver, new DirectoryKeyMap(new File("unauthorized"))),
					true
				),
				System.err
			), 4, true);
			commandFeedbackHandler = new CouchDbPacketSaver(args.createProperties(), "command_feedback");
		}
		
		Collection<MateCommand> allowedCommands = EnumSet.of(MateCommand.AUX_OFF, MateCommand.AUX_ON, MateCommand.USE, MateCommand.DROP);
		OnDataReceive onDataReceive = new MateCommandSender(
			new CommandProviderMultiplexer<>(commandProviders),
			output,
			allowedCommands,
			new OnMateCommandSent(commandFeedbackHandler)
		);
		
		List<PacketHandler> packetHandlers = new ArrayList<>();
		{
			String latestSave = args.getLatestPacketJsonSaveLocation();
			if(latestSave != null){
				packetHandlers.add(new FileWritePacketHandler(new File(latestSave), new GsonStringPacketHandler(), false));
			}
		}
		
		packetHandlers.addAll(Arrays.asList(
			latestPacketHandler,
			new ThrottleFactorPacketHandler(
				new PrintPacketHandleExceptionWrapper(getPacketSaver(args, "solarthing"), System.err),
				args.getThrottleFactor(),
				args.isOnlyInstant(),
				commandRequesterHandler
			)
		));
		
		connect(
			in,
			new MatePacketCreator49(args.getIgnoreCheckSum()),
			new PacketHandlerMultiplexer(packetHandlers),
			idGenerator,
			250,
			onDataReceive
		);
		return 0;
	}
	private int connectOuthouse(ProgramArgs args, PacketCollectionIdGenerator idGenerator) {
		List<PacketHandler> packetHandlers = new ArrayList<>();
		{
			String latestSave = args.getLatestPacketJsonSaveLocation();
			if(latestSave != null){
				packetHandlers.add(new FileWritePacketHandler(new File(latestSave), new GsonStringPacketHandler(), false));
			}
		}
		
		packetHandlers.add(new ThrottleFactorPacketHandler(getPacketSaver(args, "outhouse"), args.getThrottleFactor(), args.isOnlyInstant()));
		connect(
			System.in,
			new OuthousePacketCreator(),
			new PacketHandlerMultiplexer(packetHandlers),
			idGenerator,
			0,
			OnDataReceive.Defaults.NOTHING
		);
		return 0;
	}
	private PacketHandler getPacketSaver(ProgramArgs args, String databaseName){
		if(args.isLocal()){
			try {
				return new JsonFilePacketSaver(args.getFilePath());
			} catch (IOException e) {
				throw new RuntimeException("Incorrect file path", e);
			}
		}
		return new CouchDbPacketSaver(args.createProperties(), databaseName);
	}

	private void connect(InputStream in, PacketCreator packetCreator, PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, long samePacketTime, OnDataReceive onDataReceive) {
		Runnable run = new SolarReader(in, packetCreator, packetHandler, idGenerator, samePacketTime, onDataReceive);
		try {
			while (!Thread.currentThread().isInterrupted()) {
				run.run();
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		
		ProgramArgs pArgs = new ProgramArgs(args);
		Program program = getProgram(pArgs.getParameters());
		if(pArgs.isHelp() || program == null){
			System.out.println("<command> {solar|outhouse}");
			System.out.println("Help was called. Check ProgramArgs.java. Self explainatory. Sorry I'm lazy.\n" +
					"Also note, as a VM argument, you should have -Djava.library.path=/usr/lib/jni");
			System.exit(1);
			throw new AssertionError();
		}
		final PacketCollectionIdGenerator idGenerator;
		final Integer uniqueIdsInOneHour = pArgs.getUniqueIdsInOneHour();
		if(uniqueIdsInOneHour == null){
			idGenerator = PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR;
		} else {
			if(uniqueIdsInOneHour <= 0){
				System.err.println("--unique must be > 0 or not specified!");
				System.exit(1);
				throw new AssertionError();
			}
			idGenerator = new HourIntervalPacketCollectionIdGenerator(uniqueIdsInOneHour, new Random().nextInt());
		}
		try {
			int status = 1;
			if(program == Program.SOLAR) {
				status = (new SolarMain()).connectSolar(pArgs, idGenerator);
			} else if(program == Program.OUTHOUSE){
				status = (new SolarMain()).connectOuthouse(pArgs, idGenerator);
			} else {
				System.out.println("Specify solar|outhouse");
			}
			System.exit(status);
		} catch (Exception t) {
			t.printStackTrace();

			pArgs.printInJson();

			System.exit(1);
		}
	}
	private static Program getProgram(List<String> args){
		if(args.size() == 0){
			return null;
		}
		String program = args.get(0).toLowerCase();
		if(program.equals("solar")){
			return Program.SOLAR;
		} else if(program.equals("outhouse")){
			return Program.OUTHOUSE;
		}
		return null;
	}
	private enum Program {
		SOLAR, OUTHOUSE
	}

}
