package me.retrodaredevil.solarthing.solar.outback.command;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a command you are able to send to the Mate.
 * <p>
 * Note: Aux on/off control may not work. Some have reported that setting the Aux output function to "Remote" and turning
 * Aux to "Auto" will allow you to control it. <a href="http://outbackpower.com/forum/viewtopic.php?f=14&t=7976">outbackpower.com forum thread</a>
 * You also must make sure the Aux is not used for Generator control (AGS Control)
 */
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
	
	@Override
	public String toString() {
		return getCommandName();
	}
	
	public void send(OutputStream outputStream) throws IOException {
		outputStream.write(byteArray);
		outputStream.flush();
	}
}
