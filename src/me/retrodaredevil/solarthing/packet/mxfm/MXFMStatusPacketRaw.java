package me.retrodaredevil.solarthing.packet.mxfm;

import me.retrodaredevil.solarthing.packet.StatusPacket;

/**
 * In previous version, it was just "MX" instead of "MXFM" so MX is the same as MXFM in the documentation
 */
public interface MXFMStatusPacketRaw extends StatusPacket {
	/**
	 * Should be serialized as "address"
	 * @return The MX Address
	 */
	@Override
	int getAddress();

	/**
	 * Should be serialized as chargerCurrent
	 * <p>
	 * The DC current the MX is delivering to the batteries in Amps
	 * @return [0..99] representing the charger current in Amps
	 */
	int getChargerCurrent();

	/**
	 * Should be serialized as "pvCurrent"
	 * <p>
	 * The DC current the MX is taking from the PV panels in Amps
	 * @return [0..99] representing the PV current in Amps
	 */
	int getPVCurrent();

	/**
	 * Should be serialized as "inputVoltage"
	 * <p>
	 * The voltage seen at the MX's PV input terminals
	 * @return [0..256] The PV panel voltage (in volts)
	 */
	int getInputVoltage();

	/**
	 * Should be serialized as "dailyKWH"
	 * <p>
	 * This number is reset every morning when the MX wakes up
	 * @return [0..99.9] representing the running total of KWatt Hours produced by the PV array
	 */
	float getDailyKWH();

	/**
	 * Should be serialized as "dailyKWHString" if serialized at all
	 * @see #getDailyKWH()
	 * @return [0..99.9] in string form
	 */
	String getDailyKWHString();

	/**
	 * Should be serialized as "ampChargerCurrent"
	 * <p>
	 * Only applies to newer firmware using FlexMAX 80 or FlexMAX 60
	 * @return [0..0.9] The current to add to {@link #getChargerCurrent()} to get current displayed on FM80 or FM60
	 */
	float getAmpChargerCurrent();

	/**
	 * Should be serialized as "ampChargerCurrent" if serialized at all
	 * @see #getAmpChargerCurrent()
	 * @return The amp charger current in the format "0.X" where X is a digit [0..9]
	 */
	String getAmpChargerCurrentString();

	/**
	 * Should be serialized as "auxMode"
	 * <p>
	 * Right now, the range should only be [0..10] as there are no documented aux modes other than those 11
	 * @return [0..99] representing the {@link AuxMode}
	 */
	int getAuxMode();

	/**
	 * Should be serialized as "errorMode"
	 * @return [0..256] represents a varying number of active {@link MXFMErrorMode}s
	 */
	int getErrorMode();

	/**
	 * Right now, the range should only be [0..4] as there are no documented charger modes other than those 5
	 * @return [0..99] representing the MX's {@link ChargerMode}
	 */
	int getChargerMode();

	/**
	 * Should be serialized as "batteryVoltage"
	 * @return The battery voltage (in volts)
	 */
	float getBatteryVoltage();

	/**
	 * Should be serialized as "batteryVoltage" if serialized at all
	 * @return The battery voltage as a String
	 */
	String getBatteryVoltageString();

	/**
	 * Should be serialized as "dailyAH"
	 * <p>
	 * Only works on MATE devices with newer firmware
	 * <p>
	 * 9999 is returned if charge controller is MX60
	 * <p>
	 * 0 is always returned if this is on old firmware
	 * @return [0..2000]u[9999] The running daily total of amp hours produced by the charge controller
	 */
	int getDailyAH();
	int getChksum();
}
