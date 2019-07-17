package me.retrodaredevil.modbus;

public interface ModbusWrite {
	/**
	 * @param register The register to write to
	 * @param value The value to write to the register. Cannot be bigger than <code>(1 << 16) - 1</code>
	 */
	void writeRegister(int register, int value);
//	void writeRegister(int register, short value);
//
//	void writeRegisterAndNext(int register, int value);
//
//	/**
//	 *
//	 * @param register The base register to write
//	 * @param numberOfRegisters The number of registers to write to
//	 * @param value The value to write to the registers. Cannot be bigger than <code>1 << (numberOfRegisters * 16)</code>
//	 */
//	void writeRegisters(int register, int numberOfRegisters, long value);
//
}
