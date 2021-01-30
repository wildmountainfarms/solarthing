package me.retrodaredevil.solarthing.solar.outback.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import me.retrodaredevil.solarthing.commands.Command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a command you are able to send to the Mate.
 * <p>
 * Note: Aux on/off control may not work. Some have reported that setting the Aux output function to "Remote" and turning
 * Aux control to "Auto" will allow you to control it. <a href="https://forum.outbackpower.com/viewtopic.php?f=14&t=7976&sid=710ecc3edc75cf26584532c3b93d5aef">outbackpower.com forum thread</a>
 * You also must make sure the Aux is not used for Generator control (AGS Control)
 */
public enum MateCommand implements Command {
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
	private static final Map<String, MateCommand> MATE_COMMAND_MAP;
	static {
		Map<String, MateCommand> map = new HashMap<>();
		for (MateCommand mateCommand : values()) {
			map.put(mateCommand.getCommandName(), mateCommand);
		}
		MATE_COMMAND_MAP = Collections.unmodifiableMap(map);
	}

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
	@JsonValue
	@Override
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
	@JsonCreator
	public static MateCommand fromCommandName(String commandName) {
		MateCommand mateCommand = MATE_COMMAND_MAP.get(commandName);
		if (mateCommand == null) {
			throw new IllegalArgumentException("No Mate Command with the name: '" + commandName + '\'');
		}
		return mateCommand;
	}
}
