package me.retrodaredevil.iot.solar.mx;

public interface MXStatusPacket extends MXStatusPacketRaw {
	String getAuxModeName();
	String getErrorsString();
	String getChargerModeName();

	/**
	 * Should be serialized as "dailyKWHString" if serialized at all
	 * @see #getDailyKWH()
	 * @return [0..99.9] in string form
	 */
	String getDailyKWHString();

	/**
	 * Should be serialized as "ampChargerCurrent" if serialized at all
	 * @see #getAmpChargerCurrent()
	 * @return The amp charger current in the format "0.X" where X is a digit [0..9]
	 */
	String getAmpChargerCurrentString();

	/**
	 * Should be serialized as "batteryVoltage" if serialized at all
	 * @return The battery voltage as a String
	 */
	String getBatteryVoltageString();
}
