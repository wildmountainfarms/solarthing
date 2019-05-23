package me.retrodaredevil.iot.solar.fx;

/**
 *
 */
public interface FXStatusPacket extends FXStatusPacketRaw {
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


	/**
	 * Should be serialized as "batteryVoltageString" if serialized at all
	 * @return The battery voltage as a String
	 */
	String getBatteryVoltageString();

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
}
