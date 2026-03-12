package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@JsonClassDescription("Represents a charge controller that charges the battery and has input from PV")
@NullMarked
public interface BasicChargeController extends ChargeController, BatteryVoltage, PVCurrentAndVoltage, SolarDevice {

	// TODO remove NonNull
	@GraphQLInclude("chargingCurrent")
	@NonNull Number getChargingCurrent();

	// TODO remove NonNull
	/**
	 * Normally, this can also be calculated by multiplying {@link #getChargingCurrent()} and {@link #getBatteryVoltage()}
	 * @return The charging power in Watts
	 */
	@GraphQLInclude("chargingPower")
	@NonNull Number getChargingPower();

	// TODO remove NonNull
	@GraphQLInclude("chargingMode")
	@NonNull SolarMode getChargingMode();

	// TODO remove NonNull
	@Override
	default @NonNull SolarMode getSolarMode() {
		return getChargingMode();
	}
}
