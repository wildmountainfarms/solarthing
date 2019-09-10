package me.retrodaredevil.solarthing.config.options;

import com.lexicalscope.jewel.cli.Option;

import java.io.File;

public interface RoverOption extends IOBundleOption{
	/**
	 * @return The modbus address of the rover
	 */
	@Option(longName = {"modbus", "modbus-address"}, defaultValue = { "1" })
	int getModbusAddress();
	
	/**
	 * @return Usually null, or if non-null, this file represents a json representation of a single Renogy Rover packet, which will be used instead of {@link #getIOBundleFile()}
	 */
	@Option(longName = "dummy", defaultToNull = true)
	File getDummyFile();
}
