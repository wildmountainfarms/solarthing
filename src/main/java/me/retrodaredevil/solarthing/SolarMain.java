package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.io.IOBundle;
import me.retrodaredevil.solarthing.io.JSerialIOBundle;
import me.retrodaredevil.solarthing.io.SerialPortException;
import me.retrodaredevil.solarthing.outhouse.OuthousePacketCreator;
import me.retrodaredevil.solarthing.packets.creation.PacketCreator;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.handling.ThrottleFactorPacketHandler;
import me.retrodaredevil.solarthing.solar.outback.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SolarMain {
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
		InputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(new File("command_input.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("no command input file!");
		}
		final OnDataReceive onDataReceive;
		if(fileInputStream == null){
			onDataReceive = OnDataReceive.Defaults.NOTHING;
		} else {
			onDataReceive = new MateCommandSender(InputStreamCommandProvider.createFromList(fileInputStream, Arrays.asList(MateCommand.AUX_OFF, MateCommand.AUX_ON, MateCommand.USE, MateCommand.DROP)), output);
		}
		
		connect(
			in,
			new MatePacketCreator49(args.getIgnoreCheckSum()),
			new ThrottleFactorPacketHandler(getPacketSaver(args, "solarthing"), args.getThrottleFactor(), args.isOnlyInstant()),
			idGenerator,
			250,
			onDataReceive
		);
		return 0;
	}
	private int connectOuthouse(ProgramArgs args, PacketCollectionIdGenerator idGenerator) {
		connect(
			System.in,
			new OuthousePacketCreator(),
			new ThrottleFactorPacketHandler(getPacketSaver(args, "outhouse"), args.getThrottleFactor(), args.isOnlyInstant()),
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
