package me.retrodaredevil.solarthing.solar.outback.mx;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.*;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.VersionedPacket;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.common.BasicChargeController;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.outback.OutbackStatusPacket;

import java.util.Set;

/**
 * Represents an MX Status Packet from an Outback Mate
 * <p>
 * In previous version, it was just "MX" instead of "MXFM" so MX is the same as MXFM in the documentation. FM stands for FLEXmax.
 */
@JsonDeserialize(as = ImmutableMXStatusPacket.class)
@JsonTypeName("MXFM_STATUS")
@JsonExplicit
public interface MXStatusPacket extends OutbackStatusPacket, BasicChargeController, DailyChargeController, BatteryVoltage, VersionedPacket {

	/** This version indicates that the packet no longer has convenience string fields. This indicates a different in serialization, not in underlying data. */
	int VERSION_NO_MORE_CONVENIENCE_FIELDS = 2;
	int VERSION_LATEST = VERSION_NO_MORE_CONVENIENCE_FIELDS;

	@DefaultFinal
	@Override
	default @NotNull SolarStatusPacketType getPacketType(){
		return SolarStatusPacketType.MXFM_STATUS;
	}

	@Override
	default boolean isNewDay(DailyData previousDailyData){
		if (!(previousDailyData instanceof MXStatusPacket)) {
			throw new IllegalArgumentException("previousDailyData is not a MXStatusPacket! It's: " + previousDailyData.getClass().getName());
		}
		MXStatusPacket previous = (MXStatusPacket) previousDailyData;
		return getDailyKWH() < previous.getDailyKWH() || getDailyAH() < previous.getDailyAH();
	}

	@Override
	@Nullable
	default Long getStartDateMillis() { return null; }

	@Override
	default @NotNull Integer getPVWattage() {
		return getPVCurrent() * getPVVoltage();
	}
	// region Packet Values

	/**
	 * Should be serialized as "chargerCurrent"
	 * <p>
	 * The DC current the MX is delivering to the batteries in Amps
	 * @return [0..99] representing the charger current in Amps
	 * @deprecated Deprecated to encourage use of {@link #getChargingCurrent()}. This, however, will not be removed in a future release.
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	@JsonProperty("chargerCurrent")
	int getChargerCurrent();
	/**
	 * Should be serialized as "ampChargerCurrent"
	 * <p>
	 * Only applies to newer firmware using FlexMAX 80 or FlexMAX 60
	 * @return [0..0.9] The current to add to {@link #getChargerCurrent()} to get current displayed on FM80 or FM60
	 * @deprecated Deprecated to encourage use of {@link #getChargingCurrent()}. This, however, will not be removed in a future release
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	@JsonProperty("ampChargerCurrent")
	float getAmpChargerCurrent();

	@GraphQLInclude("chargingCurrent")
	@Override
	default @NotNull Number getChargingCurrent(){
		/*
		TODO In the future, if we decide to detect and store if we are on old firmware or a non-FM device, we may want to
		explicitly return either an Integer or Float so the caller can decide how they want to display the number.
		(They may display the number differently if it is a Integer rather than a Float)

		OR even better, have a method that returns whether or not it is supported using the Support class
		 */
		return getChargerCurrent() + getAmpChargerCurrent();
	}

	@GraphQLInclude("chargingPower")
	@Override
	default @NotNull Float getChargingPower(){
		return (getChargerCurrent() + getAmpChargerCurrent()) * getBatteryVoltage();
	}

	/**
	 * Should be serialized as "pvCurrent"
	 * <p>
	 * The DC current the MX is taking from the PV panels in Amps
	 * @return [0..99] representing the PV current in Amps
	 */
	@NotNull
	@JsonProperty("pvCurrent")
	@Override
	Integer getPVCurrent();

	/**
	 * Should be serialized as "inputVoltage"
	 * <p>
	 * The voltage seen at the MX's PV input terminals
	 * @return [0..256] The PV panel voltage (in volts)
	 */
	@NotNull
	@JsonProperty("inputVoltage")
	@Override
	Integer getPVVoltage();

	/**
	 * Should be serialized as "dailyKWH"
	 * <p>
	 * This number is reset every morning when the MX wakes up
	 * @return [0..99.9] representing the running total of KWatt Hours produced by the PV array
	 */
	@Override
	float getDailyKWH();


	/**
	 * Should be serialized as "auxMode"
	 * <p>
	 * Right now, the range should only be [0..10] as there are no documented aux modes other than those 11
	 * @return [0..99] representing the {@link AuxMode}
	 */
	@JsonProperty("auxMode")
	@GraphQLInclude("auxModeValueRaw")
	int getRawAuxModeValue();
	@GraphQLInclude("auxModeValue")
	default int getAuxModeValue() {
		return AuxMode.getActualValueCode(getRawAuxModeValue());
	}
	@GraphQLInclude("auxMode")
	default @NotNull AuxMode getAuxMode(){ return Modes.getActiveMode(AuxMode.class, getAuxModeValue());}
	@GraphQLInclude("auxBitActive")
	default boolean isAuxBitActive(){ return AuxMode.isAuxModeActive(getRawAuxModeValue()); }

	/**
	 * Should be serialized as "errorMode"
	 * @return [0..255] represents a varying number of active {@link MXErrorMode}s
	 */
	@JsonProperty("errorMode")
	@Override
	int getErrorModeValue();
	@Override
	default Set<@NotNull MXErrorMode> getErrorModes(){
		return Modes.getActiveModes(MXErrorMode.class, getErrorModeValue());
	}

	/**
	 * Should be serialized as "chargerMode"
	 *
	 * Right now, the range should only be [0..4] as there are no documented charger modes other than those 5
	 * @return [0..99] representing the MX's {@link ChargerMode}
	 */
	@JsonProperty("chargerMode")
	int getChargerModeValue();

	@GraphQLInclude("chargingMode")
	@Override
	default @NotNull ChargerMode getChargingMode(){
		return Modes.getActiveMode(ChargerMode.class, getChargerModeValue());
	}

	/**
	 * Should be serialized as "dailyAH"
	 * <p>
	 * Only works on MATE devices with newer firmware
	 * <p>
	 * Use {@link #getDailyAHSupport()} to check if this is supported
	 * <p>
	 * 9999 is returned if charge controller is MX60
	 * <p>
	 * 0 is always returned if this is on old firmware. (this only works on FLEXmax80 and FLEXmax60)
	 * @return [0..2000]u[9999] The running daily total of amp hours produced by the charge controller
	 */
	@Override
	int getDailyAH();
	/**
	 * Should be serialized as "dailyAHSupport". Should be serialized using {@link Support#toString()}
	 * @return A {@link Support} enum constant indicating whether or not {@link #getDailyAH()} is supported
	 */
	@JsonProperty("dailyAHSupport")
	@Override
	@NotNull Support getDailyAHSupport();

	/**
	 * Should be serialized as "chksum"
	 * @return The check sum for the packet
	 */
	@JsonProperty("chksum")
	int getChksum();
	// endregion

	// region Convenience Strings
	@ConvenienceField
	@JsonProperty("auxModeName")
	default @NotNull String getAuxModeName(){ return getAuxMode().getModeName(); }
	@ConvenienceField
	@JsonProperty("errors")
	default @NotNull String getErrorsString(){ return Modes.toString(MXErrorMode.class, getErrorModeValue()); }
	@ConvenienceField
	@JsonProperty("chargerModeName")
	default @NotNull String getChargerModeName(){ return getChargingMode().getModeName(); }
	// endregion

	/**
	 *
	 * @return true if the Mate's firmware is pre 4.01, false otherwise
	 */
	default boolean isOldMateFirmware(){
		// TODO Does this mean an old MX firmware, or does this mean an old MATE firmware?
		int dailyAH = getDailyAH();
		if(dailyAH == 9999){
			return false; // new MX
		}
		Support support = getDailyAHSupport(); // this should never be UNKNOWN unless we have solarthing data before summer of 2019, so this is fine
		return support != Support.FULLY_SUPPORTED;
	}

	/**
	 * @return true if this is a FlexMAX charge controller, false if it's an MX, null if the Mate has old firmware and is unable to tell
	 */
	default @Nullable Boolean isFlexMax(){
		if (isAuxBitActive() || getAuxMode().isFlexMaxOnly() || getAmpChargerCurrent() != 0) {
			return true;
		}
		if (isOldMateFirmware()) {
			return null;
		}
		return getDailyAH() != 9999;
	}

}
