package me.retrodaredevil.solarthing.io;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Deprecated
public class RXTXIOBundle implements IOBundle {
	private final InputStream inputStream;
	private final OutputStream outputStream;
	private RXTXIOBundle(SerialPort serialPort) throws SerialPortException {
		try {
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			throw new SerialPortException(e);
		}
		try {
			serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			throw new SerialPortException("Couldn't set serial port params", e);
		}
		
		serialPort.setDTR(true);
		serialPort.setRTS(false);
	}
	public static RXTXIOBundle createPort(String portName) throws SerialPortException {
		final CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		} catch (NoSuchPortException e) {
			throw new SerialPortException(e);
		}
		final CommPort commPort;
		try {
			commPort = portIdentifier.open(RXTXIOBundle.class.getName(), 2000);
		} catch (PortInUseException e) {
			throw new SerialPortException(e);
		}
		
		if (!(commPort instanceof SerialPort)) {
			throw new IllegalStateException("The port is not a serial port! It's a '" + commPort.getClass().getName() + "'");
		}
		final SerialPort serialPort = (SerialPort) commPort;
		return new RXTXIOBundle(serialPort);
	}
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
}
