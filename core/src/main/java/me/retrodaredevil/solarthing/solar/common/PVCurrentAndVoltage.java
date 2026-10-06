package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PVCurrentAndVoltage extends Identifiable {
	@JsonProperty("pvCurrent")
	Number getPVCurrent();

	/**
	 * AKA the PV Voltage
	 *
	 * Historically, this was called "inputVoltage". It will remain that way in JSON representations
	 * @return The voltage seen at the PV input terminals, usually on a charge controller
	 */
	@GraphQLInclude("pvVoltage")
	@JsonProperty("inputVoltage")
	Number getPVVoltage();

	@GraphQLInclude("pvWattage")
	default Number getPVWattage(){
		return getPVCurrent().floatValue() * getPVVoltage().floatValue();
	}
}
