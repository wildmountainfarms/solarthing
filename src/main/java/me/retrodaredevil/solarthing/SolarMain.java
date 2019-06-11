package me.retrodaredevil.solarthing;

import gnu.io.*;
import me.retrodaredevil.solarthing.outhouse.OuthousePacketCreator;
import me.retrodaredevil.solarthing.packets.PacketCreator;
import me.retrodaredevil.solarthing.packets.PacketSaver;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.solar.MatePacketCreator49;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SolarMain {
	private int connectSolar(ProgramArgs args, PacketCollectionIdGenerator idGenerator) throws Exception{
		InputStream in = null;
		try {
			in = getInputStream(args);
		} catch (PortInUseException e){
			e.printStackTrace();
			System.err.println("That port is in use!");
		} catch (NoSuchPortException e){
			e.printStackTrace();
			System.err.println("No such port: '" + args.getPortName() + "'");
		}
		if(in == null){
			return 1;
		}
		connect(args, in, "solarthing", new MatePacketCreator49(args.getIgnoreCheckSum()), idGenerator);
		return 0;
	}
	private int connectOuthouse(ProgramArgs args, PacketCollectionIdGenerator idGenerator) {
		InputStream in = System.in;
		connect(args, in, "outhouse", new OuthousePacketCreator(), idGenerator);
		return 0;
	}

	private void connect(ProgramArgs args, InputStream in, String databaseName, PacketCreator packetCreator, PacketCollectionIdGenerator idGenerator) {
		PacketSaver packetSaver;
		if(args.isLocal()){
			try {
				packetSaver = new JsonFilePacketSaver(args.getFilePath());
			} catch (IOException e) {
				throw new RuntimeException("Incorrect file path", e);
			}
		} else {
			packetSaver = new CouchDbPacketSaver(args.createProperties(), databaseName);
		}
		Runnable run = new SolarReader(in, args.getThrottleFactor(), packetCreator, packetSaver, idGenerator);
		//noinspection InfiniteLoopStatement
		while(true){
			run.run();
			try{
				Thread.sleep(5);
			} catch(InterruptedException e){
				throw new RuntimeException("We didn't expect the program to be interrupted.", e);
			}
		}
	}
	private InputStream getInputStream(ProgramArgs args) throws UnsupportedCommOperationException, IOException, PortInUseException, NoSuchPortException {
		if(args.isUnitTest()){
			return new BufferedInputStream(System.in);
		}

		final CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(args.getPortName());
		final CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

		if (!(commPort instanceof SerialPort)) {
			throw new IllegalStateException("The port is not a serial port! It's a '" + commPort.getClass().getName() + "'");
		}
		final SerialPort serialPort = (SerialPort) commPort;
		serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		serialPort.setDTR(true);
		serialPort.setRTS(false);

		return new BufferedInputStream(serialPort.getInputStream()); // TODO, if we need to, we'll have to refactor this code a bit if we want to use the output stream
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
			idGenerator = new HourIntervalPacketCollectionIdGenerator(uniqueIdsInOneHour);
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
