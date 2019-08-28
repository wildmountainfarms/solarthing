package me.retrodaredevil.modbus;

@Deprecated
public class ModbusRuntimeException extends RuntimeException {
	public ModbusRuntimeException() {
	}
	
	public ModbusRuntimeException(String message) {
		super(message);
	}
	
	public ModbusRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ModbusRuntimeException(Throwable cause) {
		super(cause);
	}
	
	public ModbusRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
