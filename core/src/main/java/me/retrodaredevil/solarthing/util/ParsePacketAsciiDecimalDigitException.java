package me.retrodaredevil.solarthing.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ParsePacketAsciiDecimalDigitException extends Exception {
	public ParsePacketAsciiDecimalDigitException(String message, String chars){
		super(message + ". chars: '" + chars + "'");
	}
}
