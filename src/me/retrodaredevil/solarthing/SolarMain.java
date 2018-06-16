package me.retrodaredevil.solarthing;

import java.io.InputStream;
import java.util.Scanner;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class SolarMain {

	void connect(ProgramArgs args) throws Exception {
		if(args.isUnitTest()){
			System.out.println("Starting in unit test mode. (No Serial port connection needed!)");
			InputStream in = System.in;
			(new Thread(new SolarReader(in, args))).start();
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
				//OutputStream out = serialPort.getOutputStream();

				(new Thread(new SolarReader(in, args))).start();
				//(new Thread(new SerialWriter(out))).start();

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
	}

}
