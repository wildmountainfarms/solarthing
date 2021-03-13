package me.retrodaredevil.solarthing.program.modbus;

import me.retrodaredevil.io.modbus.ModbusMessage;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.ModbusSlaveBus;

public class MutableAddressModbusSlave implements ModbusSlave {
	private int address;
	private final ModbusSlaveBus modbus;

	public MutableAddressModbusSlave(int startingAddress, ModbusSlaveBus modbus) {
		this.address = startingAddress;
		this.modbus = modbus;
	}

	@Override
	public ModbusMessage sendRequestMessage(ModbusMessage message) {
		return modbus.sendRequestMessage(address, message);
	}

	public void setAddress(int address) {
		this.address = address;
	}
	public int getAddress() {
		return address;
	}
}
