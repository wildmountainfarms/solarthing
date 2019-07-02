package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.solar.outback.OutbackPacket;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltagePacket;

/**
 * Represents an FX Status Packet from an Outback Mate
 */
@SuppressWarnings("unused")
public interface FXStatusPacket extends OutbackPacket, BatteryVoltagePacket {
	
	// region Packet Values
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
	// endregion
	
	// region Adjusted Currents and Voltages
	/**
	 * Should be serialized as "inverterCurrent"
	 * @return The inverter current
	 */
	int getInverterCurrent();

	/**
	 * Should be serialized as "chargerCurrent"
	 * @return The charger current
	 */
	int getChargerCurrent();

	/**
	 * Should be serialized as "buyCurrent"
	 * @return The buy current
	 */
	int getBuyCurrent();

	/**
	 * Should be serialized as "inputVoltage"
	 * @return The ac input voltage
	 */
	int getInputVoltage();

	/**
	 * Should be serialized as "outputVoltage"
	 * @return The ac output voltage
	 */
	int getOutputVoltage();

	/**
	 * Should be serialized as "sellCurrent"
	 * @return The sell current
	 */
	int getSellCurrent();
	// endregion

	// region Convenience Strings

	/**
	 * Should be serialized as "operatingModeName"
	 * @return The name of the operating mode
	 */
	String getOperatingModeName();

	/**
	 * Should be serialized as "errors"
	 * @return The errors represented as a string
	 */
	String getErrorsString();

	/**
	 * Should be serialized as "acModeName"
	 * @return The name of the ac mode
	 */
	String getACModeName();

	/**
	 * Should be serialized as "miscModes"
	 * @return The misc modes represented as a string
	 */
	String getMiscModesString();

	/**
	 * Should be serialized as "warnings"
	 * @return The warning modes represented as a string
	 */
	String getWarningsString();
	// endregion
}
