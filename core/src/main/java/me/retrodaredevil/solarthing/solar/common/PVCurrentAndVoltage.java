package me.retrodaredevil.solarthing.solar.common;

import org.jetbrains.annotations.NotNull;

public interface PVCurrentAndVoltage {
	@NotNull Number getPVCurrent();

	/**
	 * AKA the PV Voltage
	 * @return The voltage seen at the PV input terminals, usually on a charge controller
	 */
	@NotNull Number getInputVoltage();

	default @NotNull Number getPVWattage(){
		return getPVCurrent().floatValue() * getInputVoltage().floatValue();
	}
}
