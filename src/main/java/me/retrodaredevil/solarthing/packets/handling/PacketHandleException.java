package me.retrodaredevil.solarthing.packets.handling;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PacketHandleException extends Exception {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	public PacketHandleException() {
	}
	
	public PacketHandleException(String message) {
		super(message);
	}
	
	public PacketHandleException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PacketHandleException(Throwable cause) {
		super(cause);
	}
	
	public PacketHandleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public void printUnableToHandle(PrintStream printStream, String additionalReason){
		printStream.println();
		printStream.println(DATE_FORMAT.format(Calendar.getInstance().getTime()));
		printStackTrace(printStream);
		if(additionalReason != null) {
			printStream.println(additionalReason);
		}
		printStream.println();
	}
}
