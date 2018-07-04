package me.retrodaredevil.solarthing;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import me.retrodaredevil.ProgramArgs;

import java.io.InputStream;

public class SolarMain {

	public void connect(ProgramArgs args) throws Exception {
		if(args.isUnitTest()){
			System.out.println("Starting in unit test mode. (No Serial port connection needed!)");
			InputStream in = System.in;
			(new SolarReader(in, args)).start();
//			saver.start();
			System.out.println("Program is ending. Was in unit test mode.");
			return;
		}

		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(args.getPortName());
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
			
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				serialPort.setDTR(true);
				serialPort.setRTS(false);
				
				InputStream in = serialPort.getInputStream();

				(new SolarReader(in, args)).start();
				final String errorString = "Program is ending.";
				System.out.println(errorString);
				System.err.println(errorString);

			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	/*public static class SerialWriter implements Runnable {
		OutputStream out;

		public SerialWriter(OutputStream out) {
			this.out = out;
		}

		public void run() {
			try {
				int c = 0;
				while ((c = System.in.read()) > -1) {
					this.out.write(c);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/

	public static void main(String[] args) {
		ProgramArgs pArgs = new ProgramArgs(args);
		
		try {
			(new SolarMain()).connect(pArgs);
		} catch (Exception e) {
			e.printStackTrace();
			pArgs.printInJson();
		}
		System.out.println("Program is now fully exiting.");
	}

}
