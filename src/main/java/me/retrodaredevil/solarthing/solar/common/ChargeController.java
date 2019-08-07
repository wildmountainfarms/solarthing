package me.retrodaredevil.solarthing.solar.common;

public interface ChargeController extends BatteryVoltage, PVCurrentAndVoltage {
	
	Number getChargingCurrent();
	
	/**
	 * Normally, this can also be calculated by multiplying {@link #getChargingCurrent()} and {@link #getBatteryVoltage()}
	 * @return The charging power in Watts
	 */
	Number getChargingPower();
	
	
}
