package me.retrodaredevil.solarthing.solar.pzem.modbus;

import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.ErrorCodeException;
import me.retrodaredevil.io.modbus.handling.MessageHandler;
import me.retrodaredevil.io.modbus.handling.ReadHoldingRegisters;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.solar.pzem.PzemShuntReadTable;

public class PzemShuntModbusSlaveRead implements PzemShuntReadTable {
	private final ModbusSlave modbus;

	public PzemShuntModbusSlaveRead(ModbusSlave modbus) {
		this.modbus = modbus;
	}

	private static final MessageHandler<int[]> VOLTAGE = new ReadHoldingRegisters(0x0000, 1);
	private static final MessageHandler<int[]> CURRENT = new ReadHoldingRegisters(0x0001, 1);
	private static final MessageHandler<int[]> POWER = new ReadHoldingRegisters(0x0002, 2);
	private static final MessageHandler<int[]> ENERGY = new ReadHoldingRegisters(0x0004, 2);
	private static final MessageHandler<int[]> HIGH_ALARM = new ReadHoldingRegisters(0x0006, 1);
	private static final MessageHandler<int[]> LOW_ALARM = new ReadHoldingRegisters(0x0007, 1);
	private static final int EXCEPTION_ILLEGAL_FUNCTION = 1;
	private static final int EXCEPTION_ILLEGAL_ADDRESS = 2;
	private static final int EXCEPTION_ILLEGAL_DATA = 3;
	private static final int EXCEPTION_SLAVE_ERROR = 4;

	private static int convertTo32Bit(int[] arrayWithLengthOf2){
		return (arrayWithLengthOf2[1] << 16) | arrayWithLengthOf2[0]; // first register is the low value
	}
	@Override
	public int getVoltageValueRaw() {
		return modbus.sendRequestMessage(VOLTAGE)[0];
	}

	@Override
	public int getCurrentValueRaw() {
		return modbus.sendRequestMessage(CURRENT)[0];
	}

	@Override
	public int getPowerValueRaw() {
		return convertTo32Bit(modbus.sendRequestMessage(POWER));
	}

	@Override
	public @Nullable Integer getEnergyValueRaw() {
		try {
			return convertTo32Bit(modbus.sendRequestMessage(ENERGY));
		} catch (ErrorCodeException ex) {
			if (ex.getExceptionCode() == EXCEPTION_ILLEGAL_ADDRESS) {
				return null;
			}
			throw ex;
		}
	}

	@Override
	public @Nullable Integer getHighVoltageAlarmStatus() {
		try {
			return modbus.sendRequestMessage(HIGH_ALARM)[0];
		} catch (ErrorCodeException ex) {
			if (ex.getExceptionCode() == EXCEPTION_ILLEGAL_ADDRESS) {
				return null;
			}
			throw ex;
		}
	}

	@Override
	public @Nullable Integer getLowVoltageAlarmStatus() {
		try {
			return modbus.sendRequestMessage(LOW_ALARM)[0];
		} catch (ErrorCodeException ex) {
			if (ex.getExceptionCode() == EXCEPTION_ILLEGAL_ADDRESS) {
				return null;
			}
			throw ex;
		}
	}
}
