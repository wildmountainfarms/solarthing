package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonClassDescription("Represents a charge controller that charges the battery and has input from PV")
public interface BasicChargeController extends ChargeController, BatteryVoltage, PVCurrentAndVoltage, SolarDevice {

	@GraphQLInclude("chargingCurrent")
	@NotNull Number getChargingCurrent();

	/**
	 * Normally, this can also be calculated by multiplying {@link #getChargingCurrent()} and {@link #getBatteryVoltage()}
	 * @return The charging power in Watts
	 */
	@GraphQLInclude("chargingPower")
	@NotNull Number getChargingPower();

	@GraphQLInclude("chargingMode")
	@NotNull SolarMode getChargingMode();

	@Override
	default @NotNull SolarMode getSolarMode() {
		return getChargingMode();
	}
}
