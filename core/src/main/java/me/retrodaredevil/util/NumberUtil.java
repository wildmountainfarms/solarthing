package me.retrodaredevil.util;

public final class NumberUtil {
	private NumberUtil(){ throw new UnsupportedOperationException(); }
	
	/**
	 *
	 * @param raw16BitNumber The raw 16 bit number
	 * @return The upper 8 bits from {@code raw16BitNumber}
	 */
	public static int getUpper(int raw16BitNumber){
		return raw16BitNumber >>> 8;
	}
	public static byte getUpperByte(int raw16BitNumber){
		return (byte) (raw16BitNumber >>> 8);
	}
	public static int getLower(int raw16BitNumber){
		return raw16BitNumber & 0xFF;
	}
	public static byte getLowerByte(int raw16BitNumber){
		return (byte) (raw16BitNumber & 0xFF);
	}
	
	public static void checkRange(int min, int max, int check){
		if(check < min || check > max){
			throw new IllegalArgumentException(check + " is not in range [" + min + ".." + max + "]");
		}
	}
}
