package me.retrodaredevil.solarthing.solar.util;

import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.WriteSingleRegister;

import static java.util.Objects.requireNonNull;

public abstract class AbstractModbusWrite {
	protected final ModbusSlave modbus;

	protected AbstractModbusWrite(ModbusSlave modbus) {
		requireNonNull(this.modbus = modbus);
	}

	protected void write(int register, int value){
		modbus.sendRequestMessage(new WriteSingleRegister(register, value));
	}

}
