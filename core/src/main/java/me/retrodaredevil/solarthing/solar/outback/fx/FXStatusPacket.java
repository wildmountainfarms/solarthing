package me.retrodaredevil.solarthing.solar.outback.fx;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.outback.OutbackPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXMiscReporter;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXWarningReporter;

import java.util.Set;

/**
 * Represents an FX Status Packet from an Outback Mate
 */
@SuppressWarnings("unused")
@JsonDeserialize(using = ImmutableFXStatusPacket.Deserializer.class)
@JsonTypeName("FX_STATUS")
@JsonExplicit
public interface FXStatusPacket extends OutbackPacket, BatteryVoltage, FXWarningReporter, FXMiscReporter {
	
	// region Packet Values
	/**
	 * Should be serialized as "inverterCurrentRaw" if serialized at all
	 * @return The raw inverter current.
	 */
	@JsonProperty("inverterCurrentRaw")
	int getInverterCurrentRaw();
	
	/**
	 * Should be serialized as "chargerCurrentRaw" if serialized at all
	 * @return The raw charger current
	 */
	@JsonProperty("chargerCurrentRaw")
	int getChargerCurrentRaw();
	
	/**
	 * Should be serialized as "buyCurrentRaw" if serialized at all
	 * @return The raw buy current
	 */
	@JsonProperty("buyCurrentRaw")
	int getBuyCurrentRaw();
	
	/**
	 * Should be serialized as "inputVoltageRaw" if serialized at all
	 * @return The raw ac input voltage
	 */
	@JsonProperty("inputVoltageRaw")
	int getInputVoltageRaw();
	
	/**
	 * Should be serialized as "outputVoltageRaw" if serialized at all
	 * @return The raw ac output voltage
	 */
	@JsonProperty("outputVoltageRaw")
	int getOutputVoltageRaw();
	
	/**
	 * Should be serialized as "sellCurrentRaw" if serialized at all
	 * @return The raw sell current
	 */
	@JsonProperty("sellCurrentRaw")
	int getSellCurrentRaw();
	
	/**
	 * Should be serialized as "operatingMode"
	 * <p>
	 * FX Operational Mode is the same thing as FX Operating Mode. Although the serialized name is "operatingMode",
	 * "operationalMode" is the recommended name to use
	 * @return The operating mode code which represents a single OperationalMode
	 */
	@JsonProperty("operatingMode")
	int getOperationalModeValue();
	default OperationalMode getOperationalMode(){ return Modes.getActiveMode(OperationalMode.class, getOperationalModeValue()); }
	
	/**
	 * Should be serialized as "errorMode"
	 * @return The error mode bitmask which represents a varying number of ErrorModes
	 */
	@JsonProperty("errorMode")
	@Override
	int getErrorModeValue();
	@Override
	default Set<FXErrorMode> getErrorModes(){ return Modes.getActiveModes(FXErrorMode.class, getErrorModeValue()); }
	@Override
	@Deprecated
	default Set<FXErrorMode> getActiveErrors() { return getErrorModes(); }

	/**
	 * Should be serialized as "acMode"
	 * @return The AC mode code which represents a single ACMode
	 */
	@JsonProperty("acMode")
	int getACModeValue();
	default ACMode getACMode(){ return Modes.getActiveMode(ACMode.class, getACModeValue()); }

	/**
	 * Should be serialized as "misc"
	 * @return The misc mode bitmask which represents a varying number of MiscModes
	 */
	@JsonProperty("misc")
	@Override
	int getMiscValue();
	@Deprecated
	default Set<MiscMode> getActiveMiscModes(){ return Modes.getActiveModes(MiscMode.class, getMiscValue()); }
	
	/**
	 * Should be serialized as "warningMode"
	 * @return The warning mode bitmask which represents a varying number of WarningModes
	 */
	@JsonProperty("warningMode")
	@Override
	int getWarningModeValue();
	@Deprecated
	default Set<WarningMode> getActiveWarningModes(){ return getWarningModes(); }
	
	/**
	 * Should be serialized as "chksum"
	 * @return The check sum
	 */
	@JsonProperty("chksum")
	int getChksum();
	// endregion
	
	// region Adjusted Currents and Voltages
	/**
	 * Should be serialized as "inverterCurrent"
	 * @return The inverter current
	 */
	@JsonProperty("inverterCurrent")
	float getInverterCurrent();

	/**
	 * Should be serialized as "chargerCurrent"
	 * @return The charger current
	 */
	@JsonProperty("chargerCurrent")
	float getChargerCurrent();

	/**
	 * Should be serialized as "buyCurrent"
	 * @return The buy current
	 */
	@JsonProperty("buyCurrent")
	float getBuyCurrent();

	/**
	 * Should be serialized as "inputVoltage"
	 * @return The ac input voltage
	 */
	@JsonProperty("inputVoltage")
	int getInputVoltage();

	/**
	 * Should be serialized as "outputVoltage"
	 * @return The ac output voltage
	 */
	@JsonProperty("outputVoltage")
	int getOutputVoltage();

	/**
	 * Should be serialized as "sellCurrent"
	 * @return The sell current
	 */
	@JsonProperty("sellCurrent")
	float getSellCurrent();
	// endregion

	// region Convenience Strings

	/**
	 * Should be serialized as "operatingModeName"
	 * @return The name of the operating mode
	 */
	@JsonProperty("operatingModeName")
	String getOperatingModeName();

	/**
	 * Should be serialized as "errors"
	 * @return The errors represented as a string
	 */
	@JsonProperty("errors")
	String getErrorsString();

	/**
	 * Should be serialized as "acModeName"
	 * @return The name of the ac mode
	 */
	@JsonProperty("acModeName")
	String getACModeName();

	/**
	 * Should be serialized as "miscModes"
	 * @return The misc modes represented as a string
	 */
	@JsonProperty("miscModes")
	String getMiscModesString();

	/**
	 * Should be serialized as "warnings"
	 * @return The warning modes represented as a string
	 */
	@JsonProperty("warnings")
	String getWarningsString();
	// endregion

	// region Default Power Getters
	default int getInverterWattage(){
		return getInverterCurrentRaw() * getOutputVoltageRaw();
	}
	default int getChargerWattage(){
		return getChargerCurrentRaw() * getInputVoltageRaw();
	}
	default int getBuyWattage(){
		return getBuyCurrentRaw() * getInputVoltageRaw();
	}
	default int getSellWattage(){
		return getSellCurrentRaw() * getOutputVoltageRaw();
	}
	// endregion
}
