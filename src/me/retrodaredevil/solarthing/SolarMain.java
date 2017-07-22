package me.retrodaredevil.solarthing;

import java.io.InputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class SolarMain {

	void connect(String portName) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
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

				(new Thread(new SolarReader(in))).start();
				//(new Thread(new SerialWriter(out))).start();

			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	/** */

	/** */
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
		String portName = "/dev/ttyUSB0";
		if(args.length > 0){
			portName = args[0];
		}
		System.out.println("Using portName: " + portName);
		try {
			(new SolarMain()).connect(portName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
