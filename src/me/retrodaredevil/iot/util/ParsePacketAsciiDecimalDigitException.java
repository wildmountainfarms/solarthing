package me.retrodaredevil.iot.util;

public class ParsePacketAsciiDecimalDigitException extends Exception {
	public ParsePacketAsciiDecimalDigitException(String message, String chars){
		super(message + ". chars: '" + chars + "'");
	}
}
