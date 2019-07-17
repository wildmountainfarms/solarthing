package me.retrodaredevil.solarthing.solar.common;

public interface BatteryVoltage {
	
	/**
	 * Should be serialized as "batteryVoltage"
	 * @return The battery voltage as a float
	 */
	float getBatteryVoltage();
	
	/**
	 * Should be serialized as "batteryVoltageString" if serialized at all
	 * @return The battery voltage as a String
	 */
	@Deprecated
	default String getBatteryVoltageString(){
		return "" + getBatteryVoltage();
	}
	
}
