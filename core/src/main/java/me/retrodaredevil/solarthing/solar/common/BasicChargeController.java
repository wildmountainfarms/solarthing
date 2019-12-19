package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.Mode;

public interface BasicChargeController extends ChargeController, BatteryVoltage, PVCurrentAndVoltage {
	
	Number getChargingCurrent();
	
	/**
	 * Normally, this can also be calculated by multiplying {@link #getChargingCurrent()} and {@link #getBatteryVoltage()}
	 * @return The charging power in Watts
	 */
	Number getChargingPower();
	
	Mode getChargingMode();
	
	
}
