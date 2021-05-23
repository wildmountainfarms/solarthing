package me.retrodaredevil.solarthing.solar.tracer.modbus;

import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.*;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;
import me.retrodaredevil.solarthing.solar.util.AbstractModbusRead;

import static me.retrodaredevil.solarthing.solar.util.ByteUtil.convertTo48BitLittleEndian;

public class TracerModbusSlaveRead extends AbstractModbusRead implements TracerReadTable {

	public TracerModbusSlaveRead(ModbusSlave modbus) {
		super(modbus, Endian.LITTLE);
	}

	@Override
	public @NotNull Identifier getIdentifier() {
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		throw new UnsupportedOperationException("TODO");
	}

	// region 0x3000..0x300E - Rated data (read only) input register

	private static final MessageHandler<int[]> RATED_INPUT_VOLTAGE = new ReadInputRegisters(0x3000, 1);
	@Override
	public float getRatedInputVoltage() {
		return oneRegister(RATED_INPUT_VOLTAGE) / 100.0f;
	}
	private static final MessageHandler<int[]> RATED_INPUT_CURRENT = new ReadInputRegisters(0x3001, 1);
	@Override
	public float getRatedInputCurrent() {
		return oneRegister(RATED_INPUT_CURRENT);
	}

	private static final MessageHandler<int[]> RATED_INPUT_POWER = new ReadInputRegisters(0x3002, 2);
	@Override
	public float getRatedInputPower() {
		return twoRegistersAsInt(RATED_INPUT_POWER) / 100.0f;
	}

	private static final MessageHandler<int[]> RATED_OUTPUT_VOLTAGE = new ReadInputRegisters(0x3004, 1);
	@Override
	public float getRatedOutputVoltage() {
		return oneRegister(RATED_OUTPUT_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> RATED_OUTPUT_CURRENT = new ReadInputRegisters(0x3005, 1);
	@Override
	public float getRatedOutputCurrent() {
		return oneRegister(RATED_OUTPUT_CURRENT) / 100.0f;
	}

	private static final MessageHandler<int[]> RATED_OUTPUT_POWER = new ReadInputRegisters(0x3006, 2);
	@Override
	public float getRatedOutputPower() {
		return twoRegistersAsInt(RATED_OUTPUT_POWER);
	}

	/** Called Charging mode on PDF */
	private static final MessageHandler<int[]> CHARGING_TYPE = new ReadInputRegisters(0x3008, 1);
	@Override
	public int getChargingTypeValue() {
		return oneRegister(CHARGING_TYPE);
	}

	private static final MessageHandler<int[]> RATED_LOAD_OUTPUT_CURRENT = new ReadInputRegisters(0x300E, 1);
	@Override
	public float getRatedLoadOutputCurrent() {
		return oneRegister(RATED_LOAD_OUTPUT_CURRENT);
	}
	// endregion

	// region 0x3100..0x311D - Real-time data (read only) input register

	private static final MessageHandler<int[]> INPUT_VOLTAGE = new ReadInputRegisters(0x3100, 1);
	@Override
	public @NotNull Float getPVVoltage() {
		return oneRegister(INPUT_VOLTAGE) / 100.0f;
	}
	private static final MessageHandler<int[]> INPUT_CURRENT = new ReadInputRegisters(0x3101, 1);
	@Override
	public @NotNull Float getPVCurrent() {
		return oneRegister(INPUT_CURRENT) / 100.0f;
	}

	private static final MessageHandler<int[]> INPUT_POWER = new ReadInputRegisters(0x3102, 2);
	@Override
	public @NotNull Float getPVWattage() {
		return twoRegistersAsInt(INPUT_POWER) / 100.0f;
	}

	private static final MessageHandler<int[]> BATTERY_VOLTAGE = new ReadInputRegisters(0x3104, 1);
	@Override
	public float getBatteryVoltage() {
		return oneRegister(BATTERY_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> CHARGING_CURRENT = new ReadInputRegisters(0x3105, 1);
	@Override
	public @NotNull Float getChargingCurrent() {
		return oneRegister(CHARGING_CURRENT) / 100.0f;
	}

	private static final MessageHandler<int[]> CHARGING_POWER = new ReadInputRegisters(0x3106, 2);
	@Override
	public @NotNull Float getChargingPower() {
		return twoRegistersAsInt(CHARGING_POWER) / 100.0f;
	}

	private static final MessageHandler<int[]> LOAD_VOLTAGE = new ReadInputRegisters(0x310C, 1);
	@Override
	public float getLoadVoltage() {
		return oneRegister(LOAD_VOLTAGE);
	}

	private static final MessageHandler<int[]> LOAD_CURRENT = new ReadInputRegisters(0x310D, 1);
	@Override
	public float getLoadCurrent() {
		return oneRegister(LOAD_CURRENT) / 100.0f;
	}

	private static final MessageHandler<int[]> LOAD_POWER = new ReadInputRegisters(0x310E, 2);
	@Override
	public float getLoadPower() {
		return twoRegistersAsInt(LOAD_POWER);
	}

	private static final MessageHandler<int[]> BATTERY_TEMPERATURE_CELSIUS = new ReadInputRegisters(0x3110, 1);
	@Override
	public float getBatteryTemperatureCelsius() {
		return oneRegister(BATTERY_TEMPERATURE_CELSIUS) / 100.0f;
	}

	private static final MessageHandler<int[]> INSIDE_CONTROLLER_TEMPERATURE_CELSIUS = new ReadInputRegisters(0x3111, 1);
	@Override
	public float getInsideControllerTemperatureCelsius() {
		return oneRegister(INSIDE_CONTROLLER_TEMPERATURE_CELSIUS) / 100.0f;
	}

	private static final MessageHandler<int[]> POWER_COMPONENT_TEMPERATURE_CELSIUS = new ReadInputRegisters(0x3112, 1);
	@Override
	public float getPowerComponentTemperatureCelsius() {
		return oneRegister(POWER_COMPONENT_TEMPERATURE_CELSIUS) / 100.0f;
	}

	private static final MessageHandler<int[]> BATTERY_SOC = new ReadInputRegisters(0x311A, 1);
	@Override
	public int getBatterySOC() {
		return oneRegister(BATTERY_SOC); // TODO do we need to divide this one by 100? I would assume the controller gives us 0-100 not 0.00 to 100.00
	}

	private static final MessageHandler<int[]> REMOTE_BATTERY_TEMPERATURE_CELSIUS = new ReadInputRegisters(0x311B, 1);
	@Override
	public float getRemoteBatteryTemperatureCelsius() {
		return oneRegister(REMOTE_BATTERY_TEMPERATURE_CELSIUS) / 100.0f;
	}

	private static final MessageHandler<int[]> REAL_BATTERY_RATED_VOLTAGE = new ReadInputRegisters(0x311D, 1);
	@Override
	public int getRealBatteryRatedVoltageValue() {
		return oneRegister(REAL_BATTERY_RATED_VOLTAGE) / 100; // Only values should be 1200 and 2400, so we can just do integer division
	}
	// endregion

	// region 0x3200..0x3201 - Real-time status (read-only) input register
	private static final MessageHandler<int[]> BATTERY_STATUS = new ReadInputRegisters(0x3200, 1);
	@Override
	public int getBatteryStatusValue() {
		return oneRegister(BATTERY_STATUS);
	}

	private static final MessageHandler<int[]> CHARGING_EQUIPMENT_STATUS = new ReadInputRegisters(0x3201, 1);
	@Override
	public int getChargingEquipmentStatus() {
		return oneRegister(CHARGING_EQUIPMENT_STATUS);
	}
	// endregion

	// region 0x3300..0x331E - Statistical parameter (read-only) input register

	private static final MessageHandler<int[]> DAILY_MAX_INPUT_VOLTAGE = new ReadInputRegisters(0x3300, 1);
	@Override
	public float getDailyMaxPVVoltage() {
		return oneRegister(DAILY_MAX_INPUT_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> DAILY_MIN_INPUT_VOLTAGE = new ReadInputRegisters(0x3301, 1);
	@Override
	public float getDailyMinPVVoltage() {
		return oneRegister(DAILY_MIN_INPUT_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> DAILY_MAX_BATTERY_VOLTAGE = new ReadInputRegisters(0x3302, 1);
	@Override
	public float getDailyMaxBatteryVoltage() {
		return oneRegister(DAILY_MAX_BATTERY_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> DAILY_MIN_BATTERY_VOLTAGE = new ReadInputRegisters(0x3303, 1);
	@Override
	public float getDailyMinBatteryVoltage() {
		return oneRegister(DAILY_MIN_BATTERY_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> DAILY_KWH_CONSUMPTION = new ReadInputRegisters(0x3304, 2);
	@Override
	public float getDailyKWHConsumption() {
		return twoRegistersAsInt(DAILY_KWH_CONSUMPTION) / 100.0f;
	}
	private static final MessageHandler<int[]> MONTHLY_KWH_CONSUMPTION = new ReadInputRegisters(0x3306, 2);
	@Override
	public float getMonthlyKWHConsumption() {
		return twoRegistersAsInt(MONTHLY_KWH_CONSUMPTION) / 100.0f;
	}

	private static final MessageHandler<int[]> YEARLY_KWH_CONSUMPTION = new ReadInputRegisters(0x3308, 2);
	@Override
	public float getYearlyKWHConsumption() {
		return twoRegistersAsInt(YEARLY_KWH_CONSUMPTION) / 100.0f;
	}

	private static final MessageHandler<int[]> CUMULATIVE_KWH_CONSUMPTION = new ReadInputRegisters(0x330A, 2);
	@Override
	public float getCumulativeKWHConsumption() {
		return twoRegistersAsInt(CUMULATIVE_KWH_CONSUMPTION) / 100.0f;
	}



	private static final MessageHandler<int[]> DAILY_KWH = new ReadInputRegisters(0x330C, 2);
	@Override
	public float getDailyKWH() {
		return twoRegistersAsInt(DAILY_KWH) / 100.0f;
	}
	private static final MessageHandler<int[]> MONTHLY_KWH = new ReadInputRegisters(0x330E, 2);
	@Override
	public float getMonthlyKWH() {
		return twoRegistersAsInt(MONTHLY_KWH) / 100.0f;
	}

	private static final MessageHandler<int[]> YEARLY_KWH = new ReadInputRegisters(0x3310, 2);
	@Override
	public float getYearlyKWH() {
		return twoRegistersAsInt(YEARLY_KWH) / 100.0f;
	}

	private static final MessageHandler<int[]> CUMULATIVE_KWH = new ReadInputRegisters(0x3312, 2);
	@Override
	public float getCumulativeKWH() {
		return twoRegistersAsInt(CUMULATIVE_KWH) / 100.0f;
	}

	private static final MessageHandler<int[]> CARBON_DIOXIDE_REDUCTION = new ReadInputRegisters(0x3314, 2);
	@Override
	public float getCarbonDioxideReductionTons() {
		return twoRegistersAsInt(CARBON_DIOXIDE_REDUCTION) / 100.0f;
	}

	private static final MessageHandler<int[]> NET_BATTERY_CURRENT = new ReadInputRegisters(0x331B, 2);
	@Override
	public float getNetBatteryCurrent() {
		return twoRegistersAsInt(NET_BATTERY_CURRENT) / 100.0f;
	}

	private static final MessageHandler<int[]> BATTERY_TEMPERATURE_CELSIUS_331D = new ReadInputRegisters(0x331D, 1);
	@Override
	public float getBatteryTemperatureCelsius331D() {
		return twoRegistersAsInt(BATTERY_TEMPERATURE_CELSIUS_331D);
	}

	private static final MessageHandler<int[]> AMBIENT_TEMPERATURE_CELSIUS = new ReadInputRegisters(0x331E, 1);
	@Override
	public float getAmbientTemperatureCelsius() {
		return twoRegistersAsInt(AMBIENT_TEMPERATURE_CELSIUS) / 100.0f;
	}
	// endregion

	// region 0x9000..0x9070 - Setting Parameter (read-write) holding register
	private static final MessageHandler<int[]> BATTERY_TYPE = new ReadHoldingRegisters(0x9000, 1);
	@Override
	public int getBatteryTypeValue() {
		return oneRegister(BATTERY_TYPE);
	}

	private static final MessageHandler<int[]> BATTERY_CAPACITY = new ReadHoldingRegisters(0x9001, 1);
	@Override
	public int getBatteryCapacityAmpHours() {
		return oneRegister(BATTERY_CAPACITY);
	}

	private static final MessageHandler<int[]> TEMPERATURE_COMPENSATION_COEFFICIENT = new ReadHoldingRegisters(0x9002, 1);
	@Override
	public int getTemperatureCompensationCoefficient() {
		return oneRegister(TEMPERATURE_COMPENSATION_COEFFICIENT) / 100; // TODO do we have to divide this?
	}

	private static final MessageHandler<int[]> HIGH_VOLTAGE_DISCONNECT = new ReadHoldingRegisters(0x9003, 1);
	@Override
	public float getHighVoltageDisconnect() {
		return oneRegister(HIGH_VOLTAGE_DISCONNECT) / 100.0f;
	}

	private static final MessageHandler<int[]> CHARGING_LIMIT_VOLTAGE = new ReadHoldingRegisters(0x9004, 1);
	@Override
	public float getChargingLimitVoltage() {
		return oneRegister(CHARGING_LIMIT_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> OVER_VOLTAGE_RECONNECT = new ReadHoldingRegisters(0x9005, 1);
	@Override
	public float getOverVoltageReconnect() {
		return oneRegister(OVER_VOLTAGE_RECONNECT) / 100.0f;
	}

	private static final MessageHandler<int[]> EQUALIZATION_VOLTAGE = new ReadHoldingRegisters(0x9006, 1);
	@Override
	public float getEqualizationVoltage() {
		return oneRegister(EQUALIZATION_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> BOOST_VOLTAGE = new ReadHoldingRegisters(0x9007, 1);
	@Override
	public float getBoostVoltage() {
		return oneRegister(BOOST_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> FLOAT_VOLTAGE = new ReadHoldingRegisters(0x9008, 1);
	@Override
	public float getFloatVoltage() {
		return oneRegister(FLOAT_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> BOOST_RECONNECT_VOLTAGE = new ReadHoldingRegisters(0x9009, 1);
	@Override
	public float getBoostReconnectVoltage() {
		return oneRegister(BOOST_RECONNECT_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> LOW_VOLTAGE_RECONNECT = new ReadHoldingRegisters(0x900A, 1);
	@Override
	public float getLowVoltageReconnect() {
		return oneRegister(LOW_VOLTAGE_RECONNECT) / 100.0f;
	}

	private static final MessageHandler<int[]> UNDER_VOLTAGE_RECOVER = new ReadHoldingRegisters(0x900B, 1);
	@Override
	public float getUnderVoltageRecover() {
		return oneRegister(UNDER_VOLTAGE_RECOVER) / 100.0f;
	}

	private static final MessageHandler<int[]> UNDER_VOLTAGE_WARNING = new ReadHoldingRegisters(0x900C, 1);
	@Override
	public float getUnderVoltageWarning() {
		return oneRegister(UNDER_VOLTAGE_WARNING) / 100.0f;
	}

	private static final MessageHandler<int[]> LOW_VOLTAGE_DISCONNECT = new ReadHoldingRegisters(0x900D, 1);
	@Override
	public float getLowVoltageDisconnect() {
		return oneRegister(LOW_VOLTAGE_DISCONNECT) / 100.0f;
	}

	private static final MessageHandler<int[]> DISCHARGING_LIMIT_VOLTAGE = new ReadHoldingRegisters(0x900E, 1);
	@Override
	public float getDischargingLimitVoltage() {
		return oneRegister(DISCHARGING_LIMIT_VOLTAGE) / 100.0f;
	}

	private static final MessageHandler<int[]> REAL_TIME_CLOCK = new ReadHoldingRegisters(0x9013, 3);
	@Override
	public long getSecondMinuteHourDayMonthYearRaw() {
		return convertTo48BitLittleEndian(get(REAL_TIME_CLOCK));
	}

	private static final MessageHandler<int[]> EQUALIZING_CHARGING_INTERVAL = new ReadHoldingRegisters(0x9016, 1);
	@Override
	public int getEqualizationChargingCycleDays() {
		return oneRegister(EQUALIZING_CHARGING_INTERVAL);
	}

	private static final MessageHandler<int[]> BATTERY_TEMPERATURE_WARNING_UPPER_LIMIT = new ReadHoldingRegisters(0x9017, 1);
	@Override
	public float getBatteryTemperatureWarningUpperLimit() {
		return oneRegister(BATTERY_TEMPERATURE_WARNING_UPPER_LIMIT) / 100.0f;
	}

	private static final MessageHandler<int[]> BATTERY_TEMPERATURE_WARNING_LOWER_LIMIT = new ReadHoldingRegisters(0x9018, 1);
	@Override
	public float getBatteryTemperatureWarningLowerLimit() {
		return oneRegister(BATTERY_TEMPERATURE_WARNING_LOWER_LIMIT) / 100.0f;
	}

	private static final MessageHandler<int[]> INSIDE_CONTROLLER_TEMPERATURE_WARNING_UPPER_LIMIT = new ReadHoldingRegisters(0x9019, 1);
	@Override
	public float getInsideControllerTemperatureWarningUpperLimit() {
		return oneRegister(INSIDE_CONTROLLER_TEMPERATURE_WARNING_UPPER_LIMIT) / 100.0f;
	}

	private static final MessageHandler<int[]> INSIDE_CONTROLLER_TEMPERATURE_WARNING_UPPER_LIMIT_RECOVER = new ReadHoldingRegisters(0x901A, 1);
	@Override
	public float getInsideControllerTemperatureWarningUpperLimitRecover() {
		return oneRegister(INSIDE_CONTROLLER_TEMPERATURE_WARNING_UPPER_LIMIT_RECOVER) / 100.0f;
	}

	private static final MessageHandler<int[]> POWER_COMPONENT_TEMPERATURE_UPPER_LIMIT = new ReadHoldingRegisters(0x901B, 1);
	@Override
	public float getPowerComponentTemperatureUpperLimit() {
		return oneRegister(POWER_COMPONENT_TEMPERATURE_UPPER_LIMIT) / 100.0f;
	}

	private static final MessageHandler<int[]> POWER_COMPONENT_TEMPERATURE_UPPER_LIMIT_RECOVER = new ReadHoldingRegisters(0x901C, 1);
	@Override
	public float getPowerComponentTemperatureUpperLimitRecover() {
		return oneRegister(POWER_COMPONENT_TEMPERATURE_UPPER_LIMIT_RECOVER) / 100.0f;
	}

	private static final MessageHandler<int[]> LINE_IMPEDANCE = new ReadHoldingRegisters(0x901D, 1);
	@Override
	public float getLineImpedance() {
		return oneRegister(LINE_IMPEDANCE) / 100.0f;
	}

	private static final MessageHandler<int[]> NTTV = new ReadHoldingRegisters(0x901E, 1);
	@Override
	public float getNightPVVoltageThreshold() {
		return oneRegister(NTTV) / 100.0f;
	}

	private static final MessageHandler<int[]> LIGHT_SIGNAL_STARTUP_DELAY_TIME = new ReadHoldingRegisters(0x901F, 1);
	@Override
	public int getLightSignalStartupDelayTime() {
		return oneRegister(LIGHT_SIGNAL_STARTUP_DELAY_TIME);
	}

	private static final MessageHandler<int[]> DTTV = new ReadHoldingRegisters(0x9020, 1);
	@Override
	public float getDayPVVoltageThreshold() {
		return oneRegister(DTTV) / 100.0f;
	}

	private static final MessageHandler<int[]> LIGHT_SIGNAL_TURN_OFF_DELAY_TIME = new ReadHoldingRegisters(0x9021, 1);
	@Override
	public int getLightSignalTurnOffDelayTime() {
		return oneRegister(LIGHT_SIGNAL_TURN_OFF_DELAY_TIME);
	}

	private static final MessageHandler<int[]> LOAD_CONTROL_MODE = new ReadHoldingRegisters(0x903D, 1);
	@Override
	public int getLoadControlModeValue() {
		return oneRegister(LOAD_CONTROL_MODE);
	}

	private static final MessageHandler<int[]> WORKING_TIME_LENGTH_1 = new ReadHoldingRegisters(0x903E, 1);
	@Override
	public int getWorkingTimeLength1Raw() {
		return oneRegister(WORKING_TIME_LENGTH_1);
	}

	private static final MessageHandler<int[]> WORKING_TIME_LENGTH_2 = new ReadHoldingRegisters(0x903F, 1);
	@Override
	public int getWorkingTimeLength2Raw() {
		return oneRegister(WORKING_TIME_LENGTH_2);
	}

	private static final MessageHandler<int[]> TURN_ON_TIMING_1 = new ReadHoldingRegisters(0x9042, 3);
	@Override
	public long getTurnOnTiming1Raw() {
		return convertTo48BitLittleEndian(get(TURN_ON_TIMING_1));
	}

	private static final MessageHandler<int[]> TURN_OFF_TIMING_1 = new ReadHoldingRegisters(0x9045, 3);
	@Override
	public long getTurnOffTiming1Raw() {
		return convertTo48BitLittleEndian(get(TURN_OFF_TIMING_1));
	}

	private static final MessageHandler<int[]> TURN_ON_TIMING_2 = new ReadHoldingRegisters(0x9048, 3);
	@Override
	public long getTurnOnTiming2Raw() {
		return convertTo48BitLittleEndian(get(TURN_ON_TIMING_2));
	}

	private static final MessageHandler<int[]> TURN_OFF_TIMING_2 = new ReadHoldingRegisters(0x904B, 3);
	@Override
	public long getTurnOffTiming2Raw() {
		return convertTo48BitLittleEndian(get(TURN_OFF_TIMING_2));
	}

	private static final MessageHandler<int[]> LENGTH_OF_NIGHT = new ReadHoldingRegisters(0x9065, 1);
	@Override
	public int getLengthOfNightRaw() {
		return oneRegister(LENGTH_OF_NIGHT);
	}

	private static final MessageHandler<int[]> BATTERY_RATED_VOLTAGE_CODE = new ReadHoldingRegisters(0x9067, 1);
	@Override
	public int getBatteryRatedVoltageCode() {
		return oneRegister(BATTERY_RATED_VOLTAGE_CODE);
	}

	private static final MessageHandler<int[]> LOAD_TIMING_CONTROL_SELECTION = new ReadHoldingRegisters(0x9069, 1);
	@Override
	public int getLoadTimingControlSelectionValueRaw() {
		return oneRegister(LOAD_TIMING_CONTROL_SELECTION);
	}

	private static final MessageHandler<int[]> DEFAULT_LOAD_ON_OFF_IN_MANUAL_MODE = new ReadHoldingRegisters(0x906A, 1);
	@Override
	public boolean isLoadOnByDefaultInManualMode() {
		return oneRegister(DEFAULT_LOAD_ON_OFF_IN_MANUAL_MODE) == 1;
	}

	private static final MessageHandler<int[]> EQUALIZE_DURATION = new ReadHoldingRegisters(0x906B, 1);
	@Override
	public int getEqualizeDurationMinutes() {
		return oneRegister(EQUALIZE_DURATION);
	}

	private static final MessageHandler<int[]> BOOST_DURATION = new ReadHoldingRegisters(0x906C, 1);
	@Override
	public int getBoostDurationMinutes() {
		return oneRegister(BOOST_DURATION);
	}

	private static final MessageHandler<int[]> DISCHARGING_PERCENTAGE = new ReadHoldingRegisters(0x906D, 1);
	@Override
	public int getDischargingPercentage() {
		return oneRegister(DISCHARGING_PERCENTAGE); // TODO do we have to divide by 100 here?
	}

	private static final MessageHandler<int[]> CHARGING_PERCENTAGE = new ReadHoldingRegisters(0x906E, 1);
	@Override
	public int getChargingPercentage() {
		return oneRegister(CHARGING_PERCENTAGE); // TODO do we have to divide by 100 here?
	}

	private static final MessageHandler<int[]> BATTERY_MANAGEMENT_MODE = new ReadHoldingRegisters(0x9070, 1);
	@Override
	public int getBatteryManagementModeValue() {
		return oneRegister(BATTERY_MANAGEMENT_MODE);
	}
	// endregion

	// region Coils (read-write)

	private static final MessageHandler<boolean[]> MANUAL_CONTROL_LOAD = new ReadCoils(2, 1);
	@Override
	public boolean isManualLoadControlOn() {
		return modbus.sendRequestMessage(MANUAL_CONTROL_LOAD)[0];
	}

	private static final MessageHandler<boolean[]> LOAD_TEST = new ReadCoils(5, 1);
	@Override
	public boolean isLoadTestModeEnabled() {
		return modbus.sendRequestMessage(LOAD_TEST)[0];
	}

	private static final MessageHandler<boolean[]> LOAD_FORCE = new ReadCoils(6, 1);
	@Override
	public boolean isLoadForcedOn() {
		return modbus.sendRequestMessage(LOAD_FORCE)[0];
	}
	// endregion

	// region Discrete input (read-only)
	private static final MessageHandler<boolean[]> OVER_TEMPERATURE_INSIDE = new ReadDiscreteInputs(0x2000, 1);
	@Override
	public boolean isInsideControllerOverTemperature() {
		return modbus.sendRequestMessage(OVER_TEMPERATURE_INSIDE)[0];
	}

	private static final MessageHandler<boolean[]> NIGHT_DAY = new ReadDiscreteInputs(0x200C, 1);
	@Override
	public boolean isNight() {
		return modbus.sendRequestMessage(NIGHT_DAY)[0];
	}
}
