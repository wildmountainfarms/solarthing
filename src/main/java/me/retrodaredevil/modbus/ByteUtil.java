package me.retrodaredevil.modbus;

public final class ByteUtil {
	private ByteUtil(){ throw new UnsupportedOperationException(); }
	
	/**
	 *
	 * @param raw16BitNumber
	 * @return The upper 8 bits from {@code raw16BitNumber}
	 */
	public static int getUpper(int raw16BitNumber){
		return raw16BitNumber >>> 8;
	}
	public static int getLower(int raw16BitNumber){
		return raw16BitNumber & 0xFF;
	}
}
