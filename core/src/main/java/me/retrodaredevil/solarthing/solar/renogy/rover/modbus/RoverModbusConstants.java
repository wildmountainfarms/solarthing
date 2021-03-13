package me.retrodaredevil.solarthing.solar.renogy.rover.modbus;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public class RoverModbusConstants {
	private RoverModbusConstants() { throw new UnsupportedOperationException(); }


	/** The maximum amount of registers you can read at once*/
	public static final int MAX_READ_REGISTERS = 0x7D;

	public static final int MIN_ADDRESS = 1;
	public static final int MAX_ADDRESS = 247;

}
