package me.retrodaredevil.solarthing.solar.util;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class ByteUtil {
	private ByteUtil() { throw new UnsupportedOperationException(); }


	public static int upper(int number16Bit){
		return (number16Bit & 0xFF00) >> 8;
	}
	public static int lower(int number16Bit){
		return number16Bit & 0xFF;
	}
	public static int convertTo32BitBigEndian(int[] arrayWithLengthOf2){
		return (arrayWithLengthOf2[0] << 16) | arrayWithLengthOf2[1];
	}
	public static int convertTo32BitLittleEndian(int[] arrayWithLengthOf2){
		return (arrayWithLengthOf2[1] << 16) | arrayWithLengthOf2[0];
	}

	public static long convertTo48BitBigEndian(int[] arrayWithLengthOf3){
		return (((long) arrayWithLengthOf3[0]) << 32L) | (((long) arrayWithLengthOf3[1]) << 16L) | arrayWithLengthOf3[2];
	}
	public static long convertTo48BitLittleEndian(int[] arrayWithLengthOf3){
		return (((long) arrayWithLengthOf3[2]) << 32L) | (((long) arrayWithLengthOf3[1]) << 16L) | arrayWithLengthOf3[0];
	}
}
