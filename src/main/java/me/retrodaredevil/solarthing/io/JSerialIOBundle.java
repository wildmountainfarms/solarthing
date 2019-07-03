package me.retrodaredevil.solarthing.io;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import java.io.InputStream;
import java.io.OutputStream;

public class JSerialIOBundle implements IOBundle {
	private final InputStream inputStream;
	private final OutputStream outputStream;
	
	public JSerialIOBundle(SerialPort serialPort){
		serialPort.openPort(1000);
		serialPort.setComPortParameters(19200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		serialPort.setDTR();
		serialPort.clearRTS();
		inputStream = serialPort.getInputStream();
		outputStream = serialPort.getOutputStream();
	}
	
	public static JSerialIOBundle createFromPortIndex(int index) throws SerialPortException {
		final SerialPort[] ports;
		try {
			ports = SerialPort.getCommPorts();
		} catch(SerialPortInvalidPortException e){
			throw new SerialPortException(e);
		}
		if(index >= ports.length){
			throw new SerialPortException("There are only " + ports.length + " serial ports! index=" + index);
		}
		SerialPort port = ports[index];
		return new JSerialIOBundle(port);
	}
	public static JSerialIOBundle createPort(String port) throws SerialPortException {
		final SerialPort serialPort;
		try {
			serialPort = SerialPort.getCommPort(port);
		} catch(SerialPortInvalidPortException e){
			throw new SerialPortException("invalid port! port: " + port, e);
		}
		return new JSerialIOBundle(serialPort);
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
