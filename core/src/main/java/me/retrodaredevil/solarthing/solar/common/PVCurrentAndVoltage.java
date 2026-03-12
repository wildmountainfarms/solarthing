package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PVCurrentAndVoltage extends Identifiable {
	// TODO remove NonNull
	@JsonProperty("pvCurrent")
	@NonNull Number getPVCurrent();

	// TODO remove NonNull
	/**
	 * AKA the PV Voltage
	 *
	 * Historically, this was called "inputVoltage". It will remain that way in JSON representations
	 * @return The voltage seen at the PV input terminals, usually on a charge controller
	 */
	@GraphQLInclude("pvVoltage")
	@JsonProperty("inputVoltage")
	@NonNull Number getPVVoltage();

	// TODO remove NonNull
	@GraphQLInclude("pvWattage")
	default @NonNull Number getPVWattage(){
		return getPVCurrent().floatValue() * getPVVoltage().floatValue();
	}
}
