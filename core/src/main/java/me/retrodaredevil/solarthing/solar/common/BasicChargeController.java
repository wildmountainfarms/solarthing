package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.Mode;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface BasicChargeController extends ChargeController, BatteryVoltage, PVCurrentAndVoltage {

	@NotNull Number getChargingCurrent();

	/**
	 * Normally, this can also be calculated by multiplying {@link #getChargingCurrent()} and {@link #getBatteryVoltage()}
	 * @return The charging power in Watts
	 */
	@NotNull Number getChargingPower();

	@NotNull Mode getChargingMode();


}
