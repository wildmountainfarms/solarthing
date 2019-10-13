package me.retrodaredevil.solarthing.config.options;

import com.lexicalscope.jewel.cli.Option;

import java.io.File;

public interface RoverOption extends IOBundleOption{
	/**
	 * @return The modbus address of the rover
	 */
	@Option(longName = {"modbus", "modbus-address"}, defaultValue = { "1" }, description = "The address of the modbus slave")
	int getModbusAddress();
	
	/**
	 * @return Usually null, or if non-null, this file represents a json representation of a single Renogy Rover packet, which will be used instead of {@link #getIOBundleFile()}
	 */
	@Option(longName = "dummy", defaultToNull = true, description = "If specified the program will not use the --io option and will instead use the specified dummy file. This file will not be written to")
	File getDummyFile();
}
