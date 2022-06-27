package me.retrodaredevil.solarthing.util;

public class ParseUtil {
	private ParseUtil() { throw new UnsupportedOperationException(); }
	public static int toInt(char[] chars, int index) throws ParsePacketAsciiDecimalDigitException{
		int r = chars[index] - 48; // 0 is represented as ascii(48)
		if(r < 0 || r > 9){
			throw new ParsePacketAsciiDecimalDigitException(chars[index] + " is not a valid decimal digit. (index: " + index + ")", new String(chars));
		}
		return r;
	}
}
