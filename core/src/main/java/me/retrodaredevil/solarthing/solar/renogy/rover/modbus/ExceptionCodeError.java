package me.retrodaredevil.solarthing.solar.renogy.rover.modbus;

import me.retrodaredevil.solarthing.annotations.Nullable;

public enum ExceptionCodeError {
	READ_EXCEPTION_UNSUPPORTED_FUNCTION_CODE(1),
	READ_EXCEPTION_UNSUPPORTED_REGISTER(2),
	READ_EXCEPTION_TOO_MANY_REGISTERS_TO_READ(3),
	READ_EXCEPTION_CANNOT_READ_MULTIPLE_REGISTERS(4),
	;
	private final int code;

	ExceptionCodeError(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	public static @Nullable ExceptionCodeError fromCodeOrNull(int code) {
		switch (code) {
			case 1: return READ_EXCEPTION_UNSUPPORTED_FUNCTION_CODE;
			case 2: return READ_EXCEPTION_UNSUPPORTED_REGISTER;
			case 3: return READ_EXCEPTION_TOO_MANY_REGISTERS_TO_READ;
			case 4: return READ_EXCEPTION_CANNOT_READ_MULTIPLE_REGISTERS;
		}
		return null;
	}
}
