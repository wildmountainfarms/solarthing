package me.retrodaredevil.modbus.io;

public interface ModbusSlave {
	/**
	 * Sends a message to the specified slave
	 * @param address
	 * @param message
	 * @return
	 */
	ModbusMessage sendMessage(int address, ModbusMessage message);
}
