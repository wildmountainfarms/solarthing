package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.identification.Identifiable;

import javax.validation.constraints.NotNull;

public interface PVCurrentAndVoltage extends Identifiable {
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
