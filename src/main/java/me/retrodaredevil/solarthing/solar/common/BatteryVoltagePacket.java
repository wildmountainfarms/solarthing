package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.solar.SolarPacket;

public interface BatteryVoltagePacket extends SolarPacket {
	
	/**
	 * Should be serialized as "batteryVoltage"
	 * @return The battery voltage as a float
	 */
	float getBatteryVoltage();
	
	/**
	 * Should be serialized as "batteryVoltageString" if serialized at all
	 * @return The battery voltage as a String
	 */
	String getBatteryVoltageString();
}
