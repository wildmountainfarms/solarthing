package me.retrodaredevil.solarthing.config.options;

import java.io.File;

public interface RoverOption extends IOBundleOption {
	/**
	 * @return The modbus address of the rover
	 */
	int getModbusAddress();

	/**
	 * @return Usually null, or if non-null, this file represents a json representation of a single Renogy Rover packet, which will be used instead of {@link #getIOBundleFile()}
	 */
	File getDummyFile();
}
