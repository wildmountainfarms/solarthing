package me.retrodaredevil.solarthing.solar.fx;

import me.retrodaredevil.solarthing.solar.SolarPacket;

/**
 * Contains all the necessary data to calculate convenience data in {@link FXStatusPacket}
 */
public interface FXStatusPacketRaw extends SolarPacket {
	/**
	 * Should be serialized as "inverterCurrentRaw" if serialized at all
	 * @return The raw inverter current.
	 */
	int getInverterCurrentRaw();

	/**
	 * Should be serialized as "chargerCurrentRaw" if serialized at all
	 * @return The raw charger current
	 */
	int getChargerCurrentRaw();

	/**
	 * Should be serialized as "buyCurrentRaw" if serialized at all
	 * @return The raw buy current
	 */
	int getBuyCurrentRaw();

	/**
	 * Should be serialized as "inputVoltageRaw" if serialized at all
	 * @return The raw ac input voltage
	 */
	int getInputVoltageRaw();

	/**
	 * Should be serialized as "outputVoltageRaw" if serialized at all
	 * @return The raw ac output voltage
	 */
	int getOutputVoltageRaw();

	/**
	 * Should be serialized as "sellCurrentRaw" if serialized at all
	 * @return The raw sell current
	 */
	int getSellCurrentRaw();

	/**
	 * Should be serialized as "operatingMode"
	 *
	 * AKA FX operational mode
	 * @return The operating mode code which represents a single OperationalMode
	 */
	int getOperatingMode();

	/**
	 * Should be serialized as "errorMode"
	 * @return The error mode bitmask which represents a varying number of ErrorModes
	 */
	int getErrorMode();

	/**
	 * Should be serialized as "acMode"
	 * @return The AC mode code which represents a single ACMode
	 */
	int getACMode();

	/**
	 * Should be serialized as "batteryVoltage"
	 * @return The battery voltage as a float
	 */
	float getBatteryVoltage();

	/**
	 * Should be serialized as "misc"
	 * @return The misc mode bitmask which represents a varying number of MiscModes
	 */
	int getMisc();

	/**
	 * Should be serialized as "warningMode"
	 * @return The warning mode bitmask which represents a varying number of WarningModes
	 */
	int getWarningMode();

	/**
	 * Should be serialized as "chksum"
	 * @return The check sum
	 */
	int getChksum();
}
