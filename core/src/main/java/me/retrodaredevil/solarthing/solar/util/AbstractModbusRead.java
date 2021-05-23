package me.retrodaredevil.solarthing.solar.util;

import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.MessageHandler;

import static java.util.Objects.requireNonNull;
import static me.retrodaredevil.solarthing.solar.util.ByteUtil.convertTo32BitBigEndian;
import static me.retrodaredevil.solarthing.solar.util.ByteUtil.convertTo32BitLittleEndian;

public abstract class AbstractModbusRead {
	protected final ModbusSlave modbus;
	private final Endian endian;

	protected AbstractModbusRead(ModbusSlave modbus, Endian endian) {
		requireNonNull(this.modbus = modbus);
		requireNonNull(this.endian = endian);
	}

	protected int[] get(MessageHandler<int[]> readHandler){
		return modbus.sendRequestMessage(readHandler);
	}
	protected int oneRegister(MessageHandler<int[]> readHandler){
		return get(readHandler)[0];
	}
	protected int twoRegistersAsInt(MessageHandler<int[]> readHandler){
		if (endian == Endian.BIG) {
			return convertTo32BitBigEndian(get(readHandler));
		} else if (endian == Endian.LITTLE) {
			return convertTo32BitLittleEndian(get(readHandler));
		} else throw new AssertionError("Unknown endian: " + endian);
	}

	public enum Endian {
		/**
		 * The first byte is the most significant. (High byte comes first, low byte next).
		 * This makes the most sense to a human.
		 */
		BIG,
		LITTLE
	}
}
