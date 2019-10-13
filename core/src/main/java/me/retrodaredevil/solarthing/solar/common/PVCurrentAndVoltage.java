package me.retrodaredevil.solarthing.solar.common;

public interface PVCurrentAndVoltage {
	Number getPVCurrent();
	
	/**
	 * AKA the PV Voltage
	 * @return The voltage seen at the PV input terminals, usually on a charge controller
	 */
	Number getInputVoltage();
}
