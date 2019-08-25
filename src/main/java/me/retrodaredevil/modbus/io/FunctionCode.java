package me.retrodaredevil.modbus.io;

public final class FunctionCode {
	private FunctionCode(){ throw new UnsupportedOperationException(); }
	
	public static final int READ_REGISTERS = 3;
	public static final int WRITE_SINGLE_REGISTER = 6;
	public static final int WRITE_MULTIPLE_REGISTERS = 0x10;
}
