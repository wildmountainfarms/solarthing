package me.retrodaredevil.modbus;

import static me.retrodaredevil.util.NumberUtil.getLower;
import static me.retrodaredevil.util.NumberUtil.getUpper;

@Deprecated
public interface ModbusRead {
	int readRegister(int register);
	default int readRegisterUpper8Bits(int register){
		return getUpper(readRegister(register));
	}
	default int readRegisterLower8Bits(int register){
		return getLower(readRegister(register));
	}
	short readRegisterAsShort(int register);
	
	int readRegisterAndNext(int register);
	
	/**
	 *
	 * @param register The base register to read
	 * @param numberOfRegisters The amount of registers to read. Must be in range [1..4]
	 * @return
	 */
	long readRegister(int register, int numberOfRegisters);
	
	/**
	 * Each number in the array uses a maximum of 16 bits
	 * @param register The base register to read
	 * @param numberOfRegisters The number of registers to read
	 * @return An array with the same length of {@code number}
	 */
	int[] readRegistersTo16BitArray(int register, int numberOfRegisters);
	
	/**
	 * @param register The base register to read
	 * @param numberOfRegisters The number of registers to read
	 * @return An array with the same length of {@code number}
	 */
	short[] readRegistersToShortArray(int register, int numberOfRegisters);
	
	/**
	 *
	 * @param register The base register to read
	 * @param numberOfRegisters The number of registers to read
	 * @return An array with twice length of {@code number}
	 */
	short[] readRegistersTo8BitArray(int register, int numberOfRegisters);
	byte[] readRegistersToByteArray(int register, int numberOfRegisters);
	
//	void writeRegister(int register, int numberOfRegisters, long data);
}
