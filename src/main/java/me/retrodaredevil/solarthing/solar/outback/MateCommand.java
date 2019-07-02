package me.retrodaredevil.solarthing.solar.outback;

import java.io.IOException;
import java.io.OutputStream;

public enum MateCommand {
	/**
	 * Turns the FX to on mode
	 */
	ON('O', "ON"),
	/**
	 * Turns the FX to search mode
	 */
	SEARCH('S', "SEARCH"),
	/**
	 * Disallows the FX to invert. It will still charge and pass through AC depending on the AC mode
	 */
	OFF('F', "OFF"),
	/**
	 * Allows AC input to be utilized if it meets the programmed limits of the FX.
	 */
	USE('U', "USE"),
	/**
	 * Will not allow AC input to be utilized, unless there is an error. Errors will cause the FX to change the AC mode to USE.
	 */
	DROP('D', "DROP"),
	/**
	 * Energizes the 12V Aux output of the FX
	 */
	AUX_ON('Z', "AUX ON"),
	/**
	 * De-energizes the 12V Aux output of the FX
	 */
	AUX_OFF('X', "AUX OFF");
	
	private final char command;
	private final String name;
	
	private final byte[] byteArray;
	
	MateCommand(char command, String name) {
		this.command = command;
		this.name = name;
		
		byte byteCommand = (byte) command;
		this.byteArray = new byte[] {byteCommand, byteCommand};
	}
	
	public char getCommandChar(){
		return command;
	}
	public String getCompleteCommand(){
		return command + "" + command;
	}
	public String getCommandName(){
		return name;
	}
	
	public void send(OutputStream outputStream) throws IOException {
		outputStream.write(byteArray);
		outputStream.flush();
	}
}
