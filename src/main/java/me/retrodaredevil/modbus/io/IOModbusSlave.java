package me.retrodaredevil.modbus.io;

import me.retrodaredevil.solarthing.io.IOBundle;

import java.io.InputStream;
import java.io.OutputStream;

public class IOModbusSlave implements ModbusSlave {
	private final InputStream inputStream;
	private final OutputStream outputStream;
	
	private final IODataFormatter formatter;
	
	public IOModbusSlave(IOBundle ioBundle, IODataFormatter formatter) {
		this(ioBundle.getInputStream(), ioBundle.getOutputStream(), formatter);
	}
	public IOModbusSlave(InputStream inputStream, OutputStream outputStream, IODataFormatter formatter){
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.formatter = formatter;
	}
	
	@Override
	public ModbusMessage sendMessage(int address, ModbusMessage message) {
		formatter.sendMessage(outputStream, address, message);
		return formatter.readMessage(address, inputStream);
	}
}
