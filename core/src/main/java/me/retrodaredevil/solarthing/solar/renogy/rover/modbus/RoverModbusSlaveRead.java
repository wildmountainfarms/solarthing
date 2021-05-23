package me.retrodaredevil.solarthing.solar.renogy.rover.modbus;

import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.ErrorCodeException;
import me.retrodaredevil.io.modbus.handling.MessageHandler;
import me.retrodaredevil.io.modbus.handling.ReadHoldingRegisters;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverIdentifier;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverIdentityInfo;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverVariant;
import me.retrodaredevil.solarthing.solar.util.AbstractModbusRead;

import static me.retrodaredevil.solarthing.solar.util.ByteUtil.lower;
import static me.retrodaredevil.solarthing.solar.util.ByteUtil.upper;

public class RoverModbusSlaveRead extends AbstractModbusRead implements RoverReadTable {
	private static final float KWH_DIVIDER = 1_000; // units are returned in Watt Hours
	private static final int READ_EXCEPTION_UNSUPPORTED_FUNCTION_CODE = 1;
	private static final int READ_EXCEPTION_UNSUPPORTED_REGISTER = 2;
	private static final int READ_EXCEPTION_TOO_MANY_REGISTERS_TO_READ = 3;
	private static final int READ_EXCEPTION_CANNOT_READ_MULTIPLE_REGISTERS = 4;


	public RoverModbusSlaveRead(ModbusSlave modbus) {
		super(modbus, Endian.BIG);
	}
	@Override
	public @NotNull RoverIdentifier getIdentifier() {
		return RoverIdentifier.getDefaultIdentifier();
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return new RoverIdentityInfo(getRatedChargingCurrentValue(), RoverVariant.getVariant(getProductModel()));
	}


	// region 0x000s
	private static final MessageHandler<int[]> MAX_VOLTAGE_AND_CHARGING = new ReadHoldingRegisters(0x000A, 1);
	@Override
	public int getMaxVoltageValue() {
		return upper(oneRegister(MAX_VOLTAGE_AND_CHARGING));
	}
	@Override public int getRatedChargingCurrentValue() {
		return lower(oneRegister(MAX_VOLTAGE_AND_CHARGING));
	}
	private static final MessageHandler<int[]> DISCHARGING_AND_PRODUCT_TYPE = new ReadHoldingRegisters(0x000B, 1);
	@Override public int getRatedDischargingCurrentValue() {
		return upper(oneRegister(DISCHARGING_AND_PRODUCT_TYPE));
	}
	@Override public int getProductTypeValue() {
		return lower(oneRegister(DISCHARGING_AND_PRODUCT_TYPE));
	}

	private static final MessageHandler<int[]> PRODUCT_MODEL = new ReadHoldingRegisters(0x000C, 8);
	@Override public byte[] getProductModelValue() {
		int[] model = get(PRODUCT_MODEL);
		byte[] r = new byte[model.length * 2];
		for(int i = 0; i < model.length; i++){
			int data = model[i];
			r[i * 2] = (byte) upper(data);
			r[i * 2 + 1] = (byte) lower(data);
		}
		return r;
	}

	private static final MessageHandler<int[]> SOFTWARE_VERSION = new ReadHoldingRegisters(0x0014, 2);
	@Override public int getSoftwareVersionValue() {
		return twoRegistersAsInt(SOFTWARE_VERSION);
	}
	private static final MessageHandler<int[]> HARDWARE_VERSION = new ReadHoldingRegisters(0x0016, 2);
	@Override public int getHardwareVersionValue() {
		return twoRegistersAsInt(HARDWARE_VERSION);
	}
	private static final MessageHandler<int[]> PRODUCT_SERIAL = new ReadHoldingRegisters(0x0018, 2);
	@Override public int getProductSerialNumber() {
		return twoRegistersAsInt(PRODUCT_SERIAL);
	}

	private static final MessageHandler<int[]> ADDRESS = new ReadHoldingRegisters(0x001A, 1);
	@Override
	public int getControllerDeviceAddress() {
		return oneRegister(ADDRESS);
	}

	private static final MessageHandler<int[]> PROTOCOL_VERSION = new ReadHoldingRegisters(0x001B, 2);
	@Override
	public @Nullable Integer getProtocolVersionValue() {
		return twoRegistersAsInt(PROTOCOL_VERSION);
	}

	private static final MessageHandler<int[]> ID_CODE = new ReadHoldingRegisters(0x001D, 2);
	@Override
	public @Nullable Integer getUniqueIdentificationCode() {
		return twoRegistersAsInt(ID_CODE);
	}

	// endregion

	// region 0x0100s
	private static final MessageHandler<int[]> SOC = new ReadHoldingRegisters(0x0100, 1);
	@Override
	public int getBatteryCapacitySOC() {
		return oneRegister(SOC);
	}

	private static final MessageHandler<int[]> BATTERY_VOLTAGE = new ReadHoldingRegisters(0x0101, 1);
	@Override
	public float getBatteryVoltage() {
		return oneRegister(BATTERY_VOLTAGE) / 10.0F;
	}

	private static final MessageHandler<int[]> CHARGING_CURRENT = new ReadHoldingRegisters(0x0102, 1);
	@Override
	public @NotNull Float getChargingCurrent() {
		return oneRegister(CHARGING_CURRENT) / 100.0F;
	}


	private static final MessageHandler<int[]> CONTROLLER_BATTERY_TEMPERATURE = new ReadHoldingRegisters(0x0103, 1);
	@Override public int getControllerTemperatureRaw() {
		return upper(oneRegister(CONTROLLER_BATTERY_TEMPERATURE));
	}
	@Override public int getBatteryTemperatureRaw() {
		return lower(oneRegister(CONTROLLER_BATTERY_TEMPERATURE));
	}

	private static final MessageHandler<int[]> LOAD_VOLTAGE = new ReadHoldingRegisters(0x0104, 1);
	@Override
	public float getLoadVoltageRaw() {
		return oneRegister(LOAD_VOLTAGE) / 10.0F;
	}

	private static final MessageHandler<int[]> LOAD_CURRENT = new ReadHoldingRegisters(0x0105, 1);
	@Override public float getLoadCurrentRaw() {
		return oneRegister(LOAD_CURRENT) / 100.0F;
	}

	private static final MessageHandler<int[]> LOAD_POWER = new ReadHoldingRegisters(0x0106, 1);
	@Override public int getLoadPowerRaw() {
		return oneRegister(LOAD_POWER);
	}

	private static final MessageHandler<int[]> PV_VOLTAGE = new ReadHoldingRegisters(0x0107, 1);
	@NotNull
	@Override public Float getPVVoltage() { // pv voltage/solar panel voltage
		return oneRegister(PV_VOLTAGE) / 10.0F;
	}
	private static final MessageHandler<int[]> PV_CURRENT = new ReadHoldingRegisters(0x0108, 1);
	@NotNull
	@Override public Float getPVCurrent() {
		return oneRegister(PV_CURRENT) / 100.0F;
	}

	private static final MessageHandler<int[]> CHARGING_POWER = new ReadHoldingRegisters(0x0109, 1);
	@Override public @NotNull Integer getChargingPower() {
		return oneRegister(CHARGING_POWER);
	}
	// 0x010A is used as the command to turn the street light on/off
	private static final MessageHandler<int[]> DAILY_MIN_BATTERY_VOLTAGE = new ReadHoldingRegisters(0x010B, 1);
	@Override public float getDailyMinBatteryVoltage() {
		return oneRegister(DAILY_MIN_BATTERY_VOLTAGE) / 10.0F;
	}
	private static final MessageHandler<int[]> DAILY_MAX_BATTERY_VOLTAGE = new ReadHoldingRegisters(0x010C, 1);
	@Override public float getDailyMaxBatteryVoltage() {
		return oneRegister(DAILY_MAX_BATTERY_VOLTAGE) / 10.0F;
	}

	private static final MessageHandler<int[]> DAILY_MAX_CHARGING_CURRENT = new ReadHoldingRegisters(0x010D, 1);
	@Override public float getDailyMaxChargingCurrent() {
		return oneRegister(DAILY_MAX_CHARGING_CURRENT) / 100.0F;
	}
	private static final MessageHandler<int[]> DAILY_MAX_DISCHARGING_CURRENT = new ReadHoldingRegisters(0x010E, 1);
	@Override
	public float getDailyMaxDischargingCurrent() {
		return oneRegister(DAILY_MAX_DISCHARGING_CURRENT) / 100.0F;
	}
	private static final MessageHandler<int[]> DAILY_MAX_CHARGING_POWER = new ReadHoldingRegisters(0x010F, 1);
	@Override public int getDailyMaxChargingPower() {
		return oneRegister(DAILY_MAX_CHARGING_POWER);
	}
	private static final MessageHandler<int[]> DAILY_MAX_DISCHARGING_POWER = new ReadHoldingRegisters(0x0110, 1);
	@Override public int getDailyMaxDischargingPower() {
		return oneRegister(DAILY_MAX_DISCHARGING_POWER);
	}

	private static final MessageHandler<int[]> DAILY_AH_CHARGING = new ReadHoldingRegisters(0x0111, 1);
	@Override public int getDailyAH() {
		return oneRegister(DAILY_AH_CHARGING);
	}
	private static final MessageHandler<int[]> DAILY_AH_DISCHARGING = new ReadHoldingRegisters(0x0112, 1);
	@Override public int getDailyAHDischarging() {
		return oneRegister(DAILY_AH_DISCHARGING);
	}

	private static final MessageHandler<int[]> DAILY_KWH_CHARGING = new ReadHoldingRegisters(0x0113, 1);
	@Override public float getDailyKWH() {
		return oneRegister(DAILY_KWH_CHARGING) / KWH_DIVIDER;
	}

	private static final MessageHandler<int[]> DAILY_KWH_DISCHARGING = new ReadHoldingRegisters(0x0114, 1);
	@Override public float getDailyKWHConsumption() {
		return oneRegister(DAILY_KWH_DISCHARGING) / KWH_DIVIDER;
	}

	private static final MessageHandler<int[]> OPERATING_DAYS = new ReadHoldingRegisters(0x0115, 1);
	@Override public int getOperatingDaysCount() {
		return oneRegister(OPERATING_DAYS);
	}
	private static final MessageHandler<int[]> OVER_DISCHARGE_COUNT = new ReadHoldingRegisters(0x0116, 1);
	@Override public int getBatteryOverDischargesCount() {
		return oneRegister(OVER_DISCHARGE_COUNT);
	}
	private static final MessageHandler<int[]> FULL_CHARGE_COUNT = new ReadHoldingRegisters(0x0117, 1);
	@Override public int getBatteryFullChargesCount() {
		return oneRegister(FULL_CHARGE_COUNT);
	}

	private static final MessageHandler<int[]> AH_CHARGING_COUNT = new ReadHoldingRegisters(0x0118, 2);
	@Override public int getChargingAmpHoursOfBatteryCount() {
		return twoRegistersAsInt(AH_CHARGING_COUNT);
	}
	private static final MessageHandler<int[]> AH_DISCHARGING_COUNT = new ReadHoldingRegisters(0x011A, 2);
	@Override public int getDischargingAmpHoursOfBatteryCount() {
		return twoRegistersAsInt(AH_DISCHARGING_COUNT);
	}

	private static final MessageHandler<int[]> CUMULATIVE_KWH_CHARGING = new ReadHoldingRegisters(0x011C, 2);
	@Override public float getCumulativeKWH() {
		return twoRegistersAsInt(CUMULATIVE_KWH_CHARGING) / KWH_DIVIDER;
	}
	private static final MessageHandler<int[]> CUMULATIVE_KWH_DISCHARGING = new ReadHoldingRegisters(0x011E, 2);
	@Override public float getCumulativeKWHConsumption() {
		return twoRegistersAsInt(CUMULATIVE_KWH_DISCHARGING) / KWH_DIVIDER;
	}

	private static final MessageHandler<int[]> STREET_LIGHT_AND_CHARGING_STATE = new ReadHoldingRegisters(0x0120, 1);
	@Override public int getRawStreetLightValue() {
		return upper(oneRegister(STREET_LIGHT_AND_CHARGING_STATE));
	}
	@Override public int getChargingStateValue() {
		return lower(oneRegister(STREET_LIGHT_AND_CHARGING_STATE));
	}

	private static final MessageHandler<int[]> ERROR_MODE = new ReadHoldingRegisters(0x0121, 2);
	@Override public int getErrorModeValue() {
		return twoRegistersAsInt(ERROR_MODE);
	}

	// endregion

	// region 0xE000s
	private static final MessageHandler<int[]> CHARGING_CURRENT_SETTING = new ReadHoldingRegisters(0xE001, 1);

	@Override
	public @Nullable Integer getChargingCurrentSettingRaw() {
		return oneRegister(CHARGING_CURRENT_SETTING);
	}

	private static final MessageHandler<int[]> NOMINAL_BATTERY_CAPACITY = new ReadHoldingRegisters(0xE002, 1);
	@Override public int getNominalBatteryCapacity() {
		return oneRegister(NOMINAL_BATTERY_CAPACITY);
	}

	private static final MessageHandler<int[]> SYSTEM_AND_RECOGNIZED_VOLTAGE = new ReadHoldingRegisters(0xE003, 1);
	@Override public int getSystemVoltageSettingValue() {
		return upper(oneRegister(SYSTEM_AND_RECOGNIZED_VOLTAGE));
	}
	@Override public int getRecognizedVoltageValue() {
		return lower(oneRegister(SYSTEM_AND_RECOGNIZED_VOLTAGE));
	}

	private static final MessageHandler<int[]> BATTERY_TYPE = new ReadHoldingRegisters(0xE004, 1);
	@Override public int getBatteryTypeValue() {
		return oneRegister(BATTERY_TYPE);
	}

	private static final MessageHandler<int[]> OVER_VOLTAGE_THRESHOLD = new ReadHoldingRegisters(0xE005, 1);
	@Override public int getOverVoltageThresholdRaw() {
		return oneRegister(OVER_VOLTAGE_THRESHOLD);
	}

	private static final MessageHandler<int[]> CHARGING_VOLTAGE_LIMIT = new ReadHoldingRegisters(0xE006, 1);
	@Override public int getChargingVoltageLimitRaw() {
		return oneRegister(CHARGING_VOLTAGE_LIMIT);
	}

	private static final MessageHandler<int[]> EQUALIZING_CHARGING_VOLTAGE = new ReadHoldingRegisters(0xE007, 1);
	@Override public int getEqualizingChargingVoltageRaw() {
		return oneRegister(EQUALIZING_CHARGING_VOLTAGE);
	}

	private static final MessageHandler<int[]> BOOST_CHARGING_VOLTAGE = new ReadHoldingRegisters(0xE008, 1);
	@Override public int getBoostChargingVoltageRaw() {
		return oneRegister(BOOST_CHARGING_VOLTAGE);
	}

	private static final MessageHandler<int[]> FLOATING_CHARGING_VOLTAGE = new ReadHoldingRegisters(0xE009, 1);
	@Override public int getFloatingChargingVoltageRaw() {
		return oneRegister(FLOATING_CHARGING_VOLTAGE);
	}

	private static final MessageHandler<int[]> BOOST_CHARGING_RECOVERY_VOLTAGE = new ReadHoldingRegisters(0xE00A, 1);
	@Override public int getBoostChargingRecoveryVoltageRaw() {
		return oneRegister(BOOST_CHARGING_RECOVERY_VOLTAGE);
	}

	private static final MessageHandler<int[]> OVER_DISCHARGE_RECOVERY_VOLTAGE = new ReadHoldingRegisters(0xE00B, 1);
	@Override public int getOverDischargeRecoveryVoltageRaw() {
		return oneRegister(OVER_DISCHARGE_RECOVERY_VOLTAGE);
	}

	private static final MessageHandler<int[]> UNDER_VOLTAGE_WARNING_LEVEL = new ReadHoldingRegisters(0xE00C, 1);
	@Override public int getUnderVoltageWarningLevelRaw() {
		return oneRegister(UNDER_VOLTAGE_WARNING_LEVEL);
	}

	private static final MessageHandler<int[]> OVER_DISCHARGE_VOLTAGE = new ReadHoldingRegisters(0xE00D, 1);
	@Override public int getOverDischargeVoltageRaw() {
		return oneRegister(OVER_DISCHARGE_VOLTAGE);
	}

	private static final MessageHandler<int[]> DISCHARGING_LIMIT_VOLTAGE = new ReadHoldingRegisters(0xE00E, 1);
	@Override public int getDischargingLimitVoltageRaw() {
		return oneRegister(DISCHARGING_LIMIT_VOLTAGE);
	}

	private static final MessageHandler<int[]> END_OF_CHARGE_AND_DISCHARGE_SOC = new ReadHoldingRegisters(0xE00F, 1);
	@Override public int getEndOfChargeSOC() {
		return upper(oneRegister(END_OF_CHARGE_AND_DISCHARGE_SOC));
	}
	@Override public int getEndOfDischargeSOC() {
		return lower(oneRegister(END_OF_CHARGE_AND_DISCHARGE_SOC));
	}

	private static final MessageHandler<int[]> OVER_DISCHARGE_TIME_DELAY = new ReadHoldingRegisters(0xE010, 1);
	@Override public int getOverDischargeTimeDelaySeconds() {
		return oneRegister(OVER_DISCHARGE_TIME_DELAY);
	}

	private static final MessageHandler<int[]> EQUALIZING_CHARGING_TIME = new ReadHoldingRegisters(0xE011, 1);
	@Override public int getEqualizingChargingTimeRaw() {
		return oneRegister(EQUALIZING_CHARGING_TIME);
	}

	private static final MessageHandler<int[]> BOOST_CHARGING_TIME = new ReadHoldingRegisters(0xE012, 1);
	@Override public int getBoostChargingTimeRaw() {
		return oneRegister(BOOST_CHARGING_TIME);
	}

	private static final MessageHandler<int[]> EQUALIZING_CHARGING_INTERVAL = new ReadHoldingRegisters(0xE013, 1);
	@Override public int getEqualizingChargingIntervalRaw() {
		return oneRegister(EQUALIZING_CHARGING_INTERVAL);
	}

	private static final MessageHandler<int[]> TEMPERATURE_COMPENSATION = new ReadHoldingRegisters(0xE014, 1);
	@Override public int getTemperatureCompensationFactorRaw() {
		return oneRegister(TEMPERATURE_COMPENSATION);
	}

	@Override public int getOperatingDurationHours(OperatingSetting setting) {
		return oneRegister(new ReadHoldingRegisters(setting.getDurationHoursRegister(), 1));
	}
	@Override public int getOperatingPowerPercentage(OperatingSetting setting) {
		return oneRegister(new ReadHoldingRegisters(setting.getOperatingPowerPercentageRegister(), 1));
	}

	private static final MessageHandler<int[]> LOAD_WORKING_MODE = new ReadHoldingRegisters(0xE01D, 1);
	@Override public int getLoadWorkingModeValue() {
		return oneRegister(LOAD_WORKING_MODE);
	}

	private static final MessageHandler<int[]> LIGHT_CONTROL_DELAY = new ReadHoldingRegisters(0xE01E, 1);
	@Override public int getLightControlDelayMinutes() {
		return oneRegister(LIGHT_CONTROL_DELAY);
	}

	private static final MessageHandler<int[]> LIGHT_CONTROL_VOLTAGE = new ReadHoldingRegisters(0xE01F, 1);
	@Override public int getLightControlVoltage() {
		return oneRegister(LIGHT_CONTROL_VOLTAGE);
	}

	private static final MessageHandler<int[]> LED_LOAD_CURRENT_SETTING = new ReadHoldingRegisters(0xE020, 1);
	@Override public int getLEDLoadCurrentSettingRaw() {
		return oneRegister(LED_LOAD_CURRENT_SETTING);
	}

	private static final MessageHandler<int[]> SPECIAL_POWER_CONTROL_E021 = new ReadHoldingRegisters(0xE021, 1);
	@Override public int getSpecialPowerControlE021Raw() {
		return oneRegister(SPECIAL_POWER_CONTROL_E021);
	}
	@Override public @Nullable Integer getWorkingHoursRaw(Sensing sensing) {
		try {
			return oneRegister(new ReadHoldingRegisters(sensing.getWorkingHoursRegister(), 1));
		} catch (ErrorCodeException ex) {
			return null;
		}
	}
	@Override public @Nullable Integer getPowerWithPeopleSensedRaw(Sensing sensing) {
		try {
			return oneRegister(new ReadHoldingRegisters(sensing.getPowerWithPeopleSensedRegister(), 1));
		} catch (ErrorCodeException ex) {
			return null;
		}
	}
	@Override public @Nullable Integer getPowerWithNoPeopleSensedRaw(Sensing sensing) {
		try {
			return oneRegister(new ReadHoldingRegisters(sensing.getPowerWithNoPeopleSensedRegister(), 1));
		} catch (ErrorCodeException ex) {
			return null;
		}
	}

	private static final MessageHandler<int[]> SENSING_TIME_DELAY = new ReadHoldingRegisters(0xE02B, 1);
	@Override public @Nullable Integer getSensingTimeDelayRaw() {
		try {
			return oneRegister(SENSING_TIME_DELAY);
		} catch (ErrorCodeException ex) {
			return null;
		}
	}
	private static final MessageHandler<int[]> LED_LOAD_CURRENT = new ReadHoldingRegisters(0xE02C, 1);
	@Override public @Nullable Integer getLEDLoadCurrentRaw() {
		try {
			return oneRegister(LED_LOAD_CURRENT);
		} catch (ErrorCodeException ex) {
			return null;
		}
	}
	private static final MessageHandler<int[]> SPECIAL_POWER_CONTROL_E02D = new ReadHoldingRegisters(0xE02D, 1);
	@Override public @Nullable Integer getSpecialPowerControlE02DRaw() {
		try {
			return oneRegister(SPECIAL_POWER_CONTROL_E02D);
		} catch (ErrorCodeException ex) {
			return null;
		}
	}

	private static final MessageHandler<int[]> CONTROLLER_CHARGING_POWER_SETTING = new ReadHoldingRegisters(0xE02E, 1);
	@Override
	public Integer getControllerChargingPowerSetting() {
		return oneRegister(CONTROLLER_CHARGING_POWER_SETTING);
	}

	private static final MessageHandler<int[]> GENERATOR_CHARGING_POWER_SETTING = new ReadHoldingRegisters(0xE02F, 1);
	@Override
	public Integer getGeneratorChargingPowerSetting() {
		return oneRegister(GENERATOR_CHARGING_POWER_SETTING);
	}
	// endregion
}
