package me.retrodaredevil.modbus.io;

import java.io.InputStream;
import java.io.OutputStream;

public interface IODataFormatter {
	void sendMessage(OutputStream outputStream, int address, ModbusMessage message);
	ModbusMessage readMessage(int expectedAddress, InputStream inputStream);
}
