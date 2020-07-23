package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface PVCurrentAndVoltage extends Identifiable {
	@JsonProperty("pvCurrent")
	@NotNull Number getPVCurrent();

	/**
	 * AKA the PV Voltage
	 * @return The voltage seen at the PV input terminals, usually on a charge controller
	 */
	@JsonProperty("inputVoltage")
	@NotNull Number getInputVoltage();

	@GraphQLInclude("pvWattage")
	default @NotNull Number getPVWattage(){
		return getPVCurrent().floatValue() * getInputVoltage().floatValue();
	}
}
