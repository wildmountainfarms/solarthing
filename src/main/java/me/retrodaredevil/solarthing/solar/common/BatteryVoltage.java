package me.retrodaredevil.solarthing.solar.common;

import java.text.DecimalFormat;
import java.text.Format;

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
	String getBatteryVoltageString();
	
	static BatteryVoltage createTenthsBatteryVoltage(int decaVolts){
		return new BatteryVoltage() {
			final float voltage = decaVolts / 10.0f;
			final String string = new DecimalFormat("00.0").format(voltage);
			@Override
			public float getBatteryVoltage() {
				return voltage;
			}
			
			@Override
			public String getBatteryVoltageString() {
				return string;
			}
		};
	}
}
