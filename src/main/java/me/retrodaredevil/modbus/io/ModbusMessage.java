package me.retrodaredevil.modbus.io;

public interface ModbusMessage {
	/**
	 * @return The function code
	 */
	int getFunctionCode();
	byte getByteFunctionCode();
	
	/**
	 * @return An array where each element represents a single byte (8 bit number). The length of this will always be a multiple of two.
	 */
	int[] getData();
	byte[] getByteData();
}
