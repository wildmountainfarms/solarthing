package me.retrodaredevil.iot;

import me.retrodaredevil.iot.packets.PacketSaver;
import me.retrodaredevil.iot.solar.MatePacketCreator49;
import org.lightcouch.CouchDbException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import me.retrodaredevil.ProgramArgs;

public class SolarMain {

	public int connect(ProgramArgs args) throws Exception {
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
		if(in != null) {
			PacketSaver packetSaver;
			if(args.isLocal()){
				packetSaver = new JsonFilePacketSaver(args.getFilePath());
			} else {
				try {
					packetSaver = new CouchDbPacketSaver(args, "solarthing");
				} catch (CouchDbException e) {
					e.printStackTrace();
					System.err.println("Unable to connect to database.");
					packetSaver = new JsonFilePacketSaver(args.getFilePath());
				}
			}
			Runnable run = new SolarReader(in, args.getThrottleFactor(), new MatePacketCreator49(args.getIgnoreCheckSum()), packetSaver);
			while(true){
				run.run();
			}
		}
		return 1;
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
		ProgramArgs pArgs = new ProgramArgs(args);
		if(pArgs.isHelp()){
			System.out.println("Help was called. Check ProgramArgs.java. Self explainatory. Sorry I'm lazy.\n" +
					"Also note, as a VM argument, you should have -Djava.library.path=/usr/lib/jni");
			System.exit(1);
		}

		try {
			int status = (new SolarMain()).connect(pArgs);
			System.exit(status);
		} catch (Throwable t) {
			t.printStackTrace();

			pArgs.printInJson();

			System.exit(1);
		}
	}

}
