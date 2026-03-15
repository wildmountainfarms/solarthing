package me.retrodaredevil.solarthing.config.options;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface RoverOption extends IOBundleOption {
	/**
	 * @return The modbus address of the rover
	 */
	int getModbusAddress();

}
