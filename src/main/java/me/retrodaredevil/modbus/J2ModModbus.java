package me.retrodaredevil.modbus;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;

import java.io.Closeable;

import static me.retrodaredevil.util.NumberUtil.getLowerByte;
import static me.retrodaredevil.util.NumberUtil.getUpperByte;

@Deprecated
public class J2ModModbus implements ModbusReadWrite, Closeable {
	private final AbstractModbusMaster master;
	public J2ModModbus(AbstractModbusMaster master){
		this.master = master;
		try {
			master.connect();
		} catch (Exception e) {
			throw new ModbusRuntimeException(e);
		}
	}
	
	/**
	 * NOTE: For renogy products, the {@link SerialParameters#getEncoding()} should be set to <code>"rtu"</code>
	 * @param serialParameters Serial parameters for communicating over serial
	 */
	public J2ModModbus(SerialParameters serialParameters){
		this(new ModbusSerialMaster(serialParameters));
	}
	
	@Override
	public void close() {
		master.disconnect();
	}
	
	private static int convertTwo(Register r, Register r2){
		int highValue = r.toUnsignedShort();
		int lowValue = r2.toUnsignedShort();
		return (highValue << 16) | lowValue;
	}
	private static int convertTwo(Register[] registers){
		if(registers.length != 2){
			throw new IllegalArgumentException("registers.length != 2! it was: " + registers.length);
		}
		return convertTwo(registers[0], registers[1]);
	}
	public static long convert(Register... registers){
		if(registers.length > 4){
			throw new IllegalArgumentException("registers cannot be bigger than 4! length: " + registers.length);
		}
		long r = 0;
		for(int i = 0; i < registers.length; i++){
			Register register = registers[i];
			int value = register.toUnsignedShort();
			r &= value << (16 * i);
		}
		return r;
	}
	
	@Override
	public int readRegister(int register) {
		try {
			return master.readMultipleRegisters(register, 1)[0].toUnsignedShort();
		} catch (ModbusException e) {
			throw new ModbusRuntimeException(e);
		}
	}
	
	@Override
	public short readRegisterAsShort(int register) {
		try {
			return master.readMultipleRegisters(register, 1)[0].toShort();
		} catch (ModbusException e) {
			throw new ModbusRuntimeException(e);
		}
	}
	
	@Override
	public int readRegisterAndNext(int register) {
		try {
			return convertTwo(master.readMultipleRegisters(register, 2));
		} catch (ModbusException e) {
			throw new ModbusRuntimeException(e);
		}
	}
	
	@Override
	public long readRegister(int register, int numberOfRegisters) {
		try {
			return convert(master.readMultipleRegisters(register, numberOfRegisters));
		} catch (ModbusException e) {
			throw new ModbusRuntimeException(e);
		}
	}
	
	@Override
	public int[] readRegistersTo16BitArray(int register, int numberOfRegisters) {
		final Register[] registers;
		try {
			registers = master.readMultipleRegisters(register, numberOfRegisters);
		} catch (ModbusException e) {
			throw new ModbusRuntimeException(e);
		}
		int[] r = new int[registers.length];
		for(int i = 0; i < registers.length; i++){
			r[i] = registers[i].toUnsignedShort();
		}
		return r;
	}
	
	@Override
	public short[] readRegistersToShortArray(int register, int numberOfRegisters) {
		final Register[] registers;
		try {
			registers = master.readMultipleRegisters(register, numberOfRegisters);
		} catch (ModbusException e) {
			throw new ModbusRuntimeException(e);
		}
		short[] r = new short[registers.length];
		for(int i = 0; i < registers.length; i++){
			r[i] = registers[i].toShort();
		}
		return r;
	}
	
	@Override
	public short[] readRegistersTo8BitArray(int register, int numberOfRegisters) {
		byte[] bytes = readRegistersToByteArray(register, numberOfRegisters);
		short[] r = new short[bytes.length];
		for(int i = 0; i < bytes.length; i++){
			int unsigned = ((int) bytes[i]) & 0xFF;
			r[i] = (short) unsigned;
		}
		return r;
	}
	
	@Override
	public byte[] readRegistersToByteArray(int register, int numberOfRegisters) {
		byte[] bytes = new byte[numberOfRegisters * 2];
		final Register[] registers;
		try {
			registers = master.readMultipleRegisters(register, numberOfRegisters);
		} catch (ModbusException e) {
			throw new ModbusRuntimeException(e);
		}
		for(int i = 0; i < registers.length; i++){
			Register r = registers[i];
			int value = r.toUnsignedShort();
			bytes[i * 2] = getUpperByte(value);
			bytes[i * 2 + 1] = getLowerByte(value);
		}
		return bytes;
	}
	
	@Override
	public void writeRegister(int register, int value) {
		try {
			master.writeMultipleRegisters(register, new Register[]{ new SimpleRegister(value) });
		} catch (ModbusException e) {
			throw new ModbusRuntimeException(e);
		}
	}
}
