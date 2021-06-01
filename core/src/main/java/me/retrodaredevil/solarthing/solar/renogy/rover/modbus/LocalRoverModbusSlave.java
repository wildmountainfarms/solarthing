package me.retrodaredevil.solarthing.solar.renogy.rover.modbus;

import me.retrodaredevil.io.modbus.FunctionCode;
import me.retrodaredevil.io.modbus.ModbusMessage;
import me.retrodaredevil.io.modbus.ModbusMessages;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.MessageHandler;
import me.retrodaredevil.io.modbus.handling.ReadHoldingRegisters;
import me.retrodaredevil.io.modbus.handling.WriteMultipleRegisters;
import me.retrodaredevil.io.modbus.parsing.DefaultMessageParser;
import me.retrodaredevil.io.modbus.parsing.MessageParseException;
import me.retrodaredevil.io.modbus.parsing.MessageParser;
import me.retrodaredevil.solarthing.solar.renogy.rover.DummyRoverReadWrite;
import me.retrodaredevil.solarthing.solar.renogy.rover.Rover;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;

import static java.util.Objects.requireNonNull;
import static me.retrodaredevil.io.modbus.ModbusMessages.convert8BitArray;
import static me.retrodaredevil.io.modbus.ModbusMessages.get16BitDataFrom8BitArray;

/**
 * Represents a {@link ModbusSlave} tightly coupled to {@link RoverModbusSlaveRead}.
 * Note that this will not work with a ModbusCacheSlave (present in program module).
 */
public class LocalRoverModbusSlave implements ModbusSlave {
	private static final MessageParser PARSER = new DefaultMessageParser();

	private final DummyRoverReadWrite rover;

	public LocalRoverModbusSlave(RoverReadTable rover) {
		this.rover = new DummyRoverReadWrite(
				rover,
				(fieldName, previousValue, newValue) -> System.out.println(fieldName + " changed from " + previousValue + " to " + newValue)
		);
	}

	@Override
	public ModbusMessage sendRequestMessage(ModbusMessage message) {
		final MessageHandler<?> messageHandler;
		try {
			messageHandler = PARSER.parseRequestMessage(message);
		} catch (MessageParseException e) {
			throw new RuntimeException("Got error while parsing message. This should never happen because message should be from a trusted (local) source.", e);
		}

		if (messageHandler instanceof ReadHoldingRegisters) {
			ReadHoldingRegisters read = (ReadHoldingRegisters) messageHandler;
			return handleRead(read);
		} else if (messageHandler instanceof WriteMultipleRegisters) {

		}
		throw new UnsupportedOperationException("unsupported messageHandler: " + messageHandler);
	}
	private ModbusMessage handleRead(ReadHoldingRegisters read) {
		int startingAddress = read.getStartingDataAddress();
		// we don't care if the number of registers they want to read is different. That's their problem. They'll get an error.
		// TODO because we do it this way, bulk_request does not work. We can change that sometime in the future...
		if (startingAddress == RoverModbusSlaveRead.MAX_VOLTAGE_AND_CHARGING.getStartingDataAddress()) {
			int maxVoltageValue = rover.getMaxVoltageValue();
			int ratedChargingCurrentValue = rover.getRatedChargingCurrentValue();
			return read.createResponse(new int[] { maxVoltageValue << 8 | ratedChargingCurrentValue });
		} else if (startingAddress == RoverModbusSlaveRead.DISCHARGING_AND_PRODUCT_TYPE.getStartingDataAddress()) {
			int ratedDischargingCurrent = rover.getRatedDischargingCurrentValue();
			int productTypeValue = rover.getProductTypeValue();
			return read.createResponse(new int[] { ratedDischargingCurrent << 8 | productTypeValue });
		} else if (startingAddress == RoverModbusSlaveRead.PRODUCT_MODEL.getStartingDataAddress()) {
			byte[] productModel8Bit = rover.getProductModelValue();
			return read.createResponse(get16BitDataFrom8BitArray(convert8BitArray(productModel8Bit)));
		} else if (startingAddress == RoverModbusSlaveRead.SOFTWARE_VERSION.getStartingDataAddress()) {
			int softwareVersionValue = rover.getSoftwareVersionValue();
			return read.createResponse(new int[] { softwareVersionValue >> 16, softwareVersionValue & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.HARDWARE_VERSION.getStartingDataAddress()) {
			int hardwareVersionValue = rover.getHardwareVersionValue();
			return read.createResponse(new int[] { hardwareVersionValue >> 16, hardwareVersionValue & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.PRODUCT_SERIAL.getStartingDataAddress()) {
			int productSerialNumber = rover.getProductSerialNumber();
			return read.createResponse(new int[] { productSerialNumber >> 16, productSerialNumber & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.ADDRESS.getStartingDataAddress()) {
			int address = rover.getControllerDeviceAddress();
			return read.createResponse(new int[] { address });
		} else if (startingAddress == RoverModbusSlaveRead.PROTOCOL_VERSION.getStartingDataAddress()) {
			Integer protocolVersion = rover.getProtocolVersionValue();
			requireNonNull(protocolVersion, "If this is called, our RoverReadTable should support this, which is does not currently. rover: " + rover);
			return read.createResponse(new int[] { protocolVersion >> 16, protocolVersion & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.ID_CODE.getStartingDataAddress()) {
			Integer idCode = rover.getUniqueIdentificationCode();
			requireNonNull(idCode, "If this is called, our RoverReadTable should support this, which is does not currently. rover: " + rover);
			return read.createResponse(new int[] { idCode >> 16, idCode & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.SOC.getStartingDataAddress()) {
			int soc = rover.getBatteryCapacitySOC();
			return read.createResponse(new int[] { soc });
		} else if (startingAddress == RoverModbusSlaveRead.BATTERY_VOLTAGE.getStartingDataAddress()) {
			int batteryVoltageRaw = Math.round(rover.getBatteryVoltage() * 10.0f);
			return read.createResponse(new int[] { batteryVoltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.CHARGING_CURRENT.getStartingDataAddress()) {
			int chargingCurrentRaw = Math.round(rover.getChargingCurrent() * 100.0f);
			return read.createResponse(new int[] { chargingCurrentRaw });
		} else if (startingAddress == RoverModbusSlaveRead.CONTROLLER_BATTERY_TEMPERATURE.getStartingDataAddress()) {
			int controllerTemperatureRaw = rover.getControllerTemperatureRaw();
			int batteryTemperatureRaw = rover.getBatteryTemperatureRaw();
			return read.createResponse(new int[] { controllerTemperatureRaw << 8 | batteryTemperatureRaw });
		} else if (startingAddress == RoverModbusSlaveRead.LOAD_VOLTAGE.getStartingDataAddress()) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.LOAD_CURRENT.getStartingDataAddress()) {
			int loadCurrentRaw = Math.round(rover.getLoadCurrentRaw() * 10.0f);
			return read.createResponse(new int[] { loadCurrentRaw });
		} else if (startingAddress == RoverModbusSlaveRead.LOAD_POWER.getStartingDataAddress()) {
			int loadPower = rover.getLoadPowerRaw();
			return read.createResponse(new int[] { loadPower });
		} else if (startingAddress == RoverModbusSlaveRead.PV_VOLTAGE.getStartingDataAddress()) {
			int pvVoltageRaw = Math.round(rover.getPVVoltage() * 10.0f);
			return read.createResponse(new int[] { pvVoltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.PV_CURRENT.getStartingDataAddress()) {
			int pvCurrentRaw = Math.round(rover.getPVCurrent() * 100.0f);
			return read.createResponse(new int[] { pvCurrentRaw });
		} else if (startingAddress == RoverModbusSlaveRead.CHARGING_POWER.getStartingDataAddress()) {
			int chargingPower = rover.getChargingPower();
			return read.createResponse(new int[] { chargingPower });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_MIN_BATTERY_VOLTAGE.getStartingDataAddress()) {
			int batteryVoltageRaw = Math.round(rover.getDailyMinBatteryVoltage() * 10.0f);
			return read.createResponse(new int[] { batteryVoltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_MAX_BATTERY_VOLTAGE.getStartingDataAddress()) {
			int batteryVoltageRaw = Math.round(rover.getDailyMaxBatteryVoltage() * 10.0f);
			return read.createResponse(new int[] { batteryVoltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_MAX_CHARGING_CURRENT.getStartingDataAddress()) {
			int chargingCurrentRaw = Math.round(rover.getDailyMaxChargingCurrent() * 100.0f);
			return read.createResponse(new int[] { chargingCurrentRaw });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_MAX_DISCHARGING_CURRENT.getStartingDataAddress()) {
			int dischargingCurrentRaw = Math.round(rover.getDailyMaxDischargingCurrent() * 100.0f);
			return read.createResponse(new int[] { dischargingCurrentRaw });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_MAX_CHARGING_POWER.getStartingDataAddress()) {
			int chargingPower = rover.getDailyMaxChargingPower();
			return read.createResponse(new int[] { chargingPower });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_MAX_DISCHARGING_POWER.getStartingDataAddress()) {
			int chargingPower = rover.getDailyMaxDischargingPower();
			return read.createResponse(new int[] { chargingPower });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_AH_CHARGING.getStartingDataAddress()) {
			int dailyAH = rover.getDailyAH();
			return read.createResponse(new int[] { dailyAH });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_AH_DISCHARGING.getStartingDataAddress()) {
			int dailyAH = rover.getDailyAHDischarging();
			return read.createResponse(new int[] { dailyAH });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_KWH_CHARGING.getStartingDataAddress()) {
			int wattHours = Math.round(rover.getDailyKWH() * 1000.0f);
			return read.createResponse(new int[] { wattHours });
		} else if (startingAddress == RoverModbusSlaveRead.DAILY_KWH_DISCHARGING.getStartingDataAddress()) {
			int wattHours = Math.round(rover.getDailyKWHConsumption() * 1000.0f);
			return read.createResponse(new int[] { wattHours });
		} else if (startingAddress == RoverModbusSlaveRead.OPERATING_DAYS.getStartingDataAddress()) {
			int operatingDays = rover.getOperatingDaysCount();
			return read.createResponse(new int[] { operatingDays });
		} else if (startingAddress == RoverModbusSlaveRead.OVER_DISCHARGE_COUNT.getStartingDataAddress()) {
			int overDischargeCount = rover.getBatteryOverDischargesCount();
			return read.createResponse(new int[] { overDischargeCount });
		} else if (startingAddress == RoverModbusSlaveRead.FULL_CHARGE_COUNT.getStartingDataAddress()) {
			int fullChargeCount = rover.getBatteryFullChargesCount();
			return read.createResponse(new int[] { fullChargeCount });
		} else if (startingAddress == RoverModbusSlaveRead.AH_CHARGING_COUNT.getStartingDataAddress()) {
			int ampHours = rover.getDailyAH();
			return read.createResponse(new int[] { ampHours >> 16, ampHours & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.AH_DISCHARGING_COUNT.getStartingDataAddress()) {
			int ampHours = rover.getDailyAHDischarging();
			return read.createResponse(new int[] { ampHours >> 16, ampHours & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.CUMULATIVE_KWH_CHARGING.getStartingDataAddress()) {
			int wattHours = Math.round(rover.getCumulativeKWH() * 1000.0f);
			return read.createResponse(new int[] { wattHours >> 16, wattHours & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.CUMULATIVE_KWH_DISCHARGING.getStartingDataAddress()) {
			int wattHours = Math.round(rover.getCumulativeKWHConsumption() * 1000.0f);
			return read.createResponse(new int[] { wattHours >> 16, wattHours & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.STREET_LIGHT_AND_CHARGING_STATE.getStartingDataAddress()) {
			int rawStreetLightValue = rover.getRawStreetLightValue();
			int chargingStateValue = rover.getChargingStateValue();
			return read.createResponse(new int[] { rawStreetLightValue << 8 | chargingStateValue });
		} else if (startingAddress == RoverModbusSlaveRead.ERROR_MODE.getStartingDataAddress()) {
			int errorMode = rover.getErrorModeValue();
			return read.createResponse(new int[] { errorMode >> 16, errorMode & 0xFFFF });
		} else if (startingAddress == RoverModbusSlaveRead.CHARGING_CURRENT_SETTING.getStartingDataAddress()) {
			Integer chargingCurrentSettingRaw = rover.getChargingCurrentSettingRaw();
			requireNonNull(chargingCurrentSettingRaw, "If this is called, our RoverReadTable should support this, which is does not currently. rover: " + rover);
			return read.createResponse(new int[] { chargingCurrentSettingRaw });
		} else if (startingAddress == RoverModbusSlaveRead.NOMINAL_BATTERY_CAPACITY.getStartingDataAddress()) {
			int ampHours = rover.getNominalBatteryCapacity();
			return read.createResponse(new int[] { ampHours });
		} else if (startingAddress == RoverModbusSlaveRead.SYSTEM_AND_RECOGNIZED_VOLTAGE.getStartingDataAddress()) {
			int systemVoltageValue = rover.getSystemVoltageSettingValue();
			int recognizedVoltageValue = rover.getRecognizedVoltageValue();
			return read.createResponse(new int[] { systemVoltageValue << 8 | recognizedVoltageValue });
		} else if (startingAddress == RoverModbusSlaveRead.BATTERY_TYPE.getStartingDataAddress()) {
			int batteryType = rover.getBatteryTypeValue();
			return read.createResponse(new int[] { batteryType });
		} else if (startingAddress == RoverModbusSlaveRead.OVER_VOLTAGE_THRESHOLD.getStartingDataAddress()) {
			int thresholdRaw = rover.getOverVoltageThresholdRaw();
			return read.createResponse(new int[] { thresholdRaw });
		} else if (startingAddress == RoverModbusSlaveRead.CHARGING_VOLTAGE_LIMIT.getStartingDataAddress()) {
			int thresholdRaw = rover.getChargingVoltageLimitRaw();
			return read.createResponse(new int[] { thresholdRaw });
		} else if (startingAddress == RoverModbusSlaveRead.EQUALIZING_CHARGING_VOLTAGE.getStartingDataAddress()) {
			int voltageRaw = rover.getEqualizingChargingVoltageRaw();
			return read.createResponse(new int[] { voltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.BOOST_CHARGING_VOLTAGE.getStartingDataAddress()) {
			int voltageRaw = rover.getBoostChargingTimeRaw();
			return read.createResponse(new int[] { voltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.FLOATING_CHARGING_VOLTAGE.getStartingDataAddress()) {
			int voltageRaw = rover.getFloatingChargingVoltageRaw();
			return read.createResponse(new int[] { voltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.BOOST_CHARGING_RECOVERY_VOLTAGE.getStartingDataAddress()) {
			int voltageRaw = rover.getBoostChargingRecoveryVoltageRaw();
			return read.createResponse(new int[] { voltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.OVER_DISCHARGE_RECOVERY_VOLTAGE.getStartingDataAddress()) {
			int voltageRaw = rover.getOverDischargeRecoveryVoltageRaw();
			return read.createResponse(new int[] { voltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.UNDER_VOLTAGE_WARNING_LEVEL.getStartingDataAddress()) {
			int voltageRaw = rover.getUnderVoltageWarningLevelRaw();
			return read.createResponse(new int[] { voltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.OVER_DISCHARGE_VOLTAGE.getStartingDataAddress()) {
			int voltageRaw = rover.getOverDischargeVoltageRaw();
			return read.createResponse(new int[] { voltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.DISCHARGING_LIMIT_VOLTAGE.getStartingDataAddress()) {
			int voltageRaw = rover.getDischargingLimitVoltageRaw();
			return read.createResponse(new int[] { voltageRaw });
		} else if (startingAddress == RoverModbusSlaveRead.END_OF_CHARGE_AND_DISCHARGE_SOC.getStartingDataAddress()) {
			int endOfChargeSOC = rover.getEndOfChargeSOC();
			int endOfDischargeSOC = rover.getEndOfDischargeSOC();
			return read.createResponse(new int[] { endOfChargeSOC << 8 | endOfDischargeSOC });
		} else if (startingAddress == RoverModbusSlaveRead.OVER_DISCHARGE_TIME_DELAY.getStartingDataAddress()) {
			int overDischargeTimeDelay = rover.getOverDischargeTimeDelaySeconds();
			return read.createResponse(new int[] { overDischargeTimeDelay });
		} else if (startingAddress == RoverModbusSlaveRead.EQUALIZING_CHARGING_TIME.getStartingDataAddress()) {
			int chargingTimeRaw = rover.getEqualizingChargingTimeRaw();
			return read.createResponse(new int[] { chargingTimeRaw });
		} else if (startingAddress == RoverModbusSlaveRead.BOOST_CHARGING_TIME.getStartingDataAddress()) {
			int chargingTimeRaw = rover.getBoostChargingTimeRaw();
			return read.createResponse(new int[] { chargingTimeRaw });
		} else if (startingAddress == RoverModbusSlaveRead.EQUALIZING_CHARGING_INTERVAL.getStartingDataAddress()) {
			int equalizationChargingInterval = rover.getEqualizingChargingIntervalRaw();
			return read.createResponse(new int[] { equalizationChargingInterval });
		} else if (startingAddress == RoverModbusSlaveRead.TEMPERATURE_COMPENSATION.getStartingDataAddress()) {
			int compensationRaw = rover.getTemperatureCompensationFactorRaw();
			return read.createResponse(new int[] { compensationRaw });
		} else if (startingAddress == Rover.OperatingSetting.STAGE_1.getDurationHoursRegister()) {
			int durationHours = rover.getOperatingDurationHours(Rover.OperatingSetting.STAGE_1);
			return read.createResponse(new int[] { durationHours });
		} else if (startingAddress == Rover.OperatingSetting.STAGE_2.getDurationHoursRegister()) {
			int durationHours = rover.getOperatingDurationHours(Rover.OperatingSetting.STAGE_2);
			return read.createResponse(new int[] { durationHours });
		} else if (startingAddress == Rover.OperatingSetting.STAGE_3.getDurationHoursRegister()) {
			int durationHours = rover.getOperatingDurationHours(Rover.OperatingSetting.STAGE_3);
			return read.createResponse(new int[] { durationHours });
		} else if (startingAddress == Rover.OperatingSetting.MORNING_ON.getDurationHoursRegister()) {
			int durationHours = rover.getOperatingDurationHours(Rover.OperatingSetting.MORNING_ON);
			return read.createResponse(new int[] { durationHours });
		} else if (startingAddress == Rover.OperatingSetting.STAGE_1.getOperatingPowerPercentageRegister()) {
			int operatingPowerPercentage = rover.getOperatingDurationHours(Rover.OperatingSetting.STAGE_1);
			return read.createResponse(new int[] { operatingPowerPercentage });
		} else if (startingAddress == Rover.OperatingSetting.STAGE_2.getOperatingPowerPercentageRegister()) {
			int operatingPowerPercentage = rover.getOperatingDurationHours(Rover.OperatingSetting.STAGE_2);
			return read.createResponse(new int[] { operatingPowerPercentage });
		} else if (startingAddress == Rover.OperatingSetting.STAGE_3.getOperatingPowerPercentageRegister()) {
			int operatingPowerPercentage = rover.getOperatingDurationHours(Rover.OperatingSetting.STAGE_3);
			return read.createResponse(new int[] { operatingPowerPercentage });
		} else if (startingAddress == Rover.OperatingSetting.MORNING_ON.getOperatingPowerPercentageRegister()) {
			int operatingPowerPercentage = rover.getOperatingDurationHours(Rover.OperatingSetting.MORNING_ON);
			return read.createResponse(new int[] { operatingPowerPercentage });
		} else if (startingAddress == RoverModbusSlaveRead.LOAD_WORKING_MODE.getStartingDataAddress()) {
			int value = rover.getLoadWorkingModeValue();
			return read.createResponse(new int[] { value });
		} else if (startingAddress == RoverModbusSlaveRead.LIGHT_CONTROL_DELAY.getStartingDataAddress()) {
			int value = rover.getLightControlDelayMinutes();
			return read.createResponse(new int[] { value });
		} else if (startingAddress == RoverModbusSlaveRead.LIGHT_CONTROL_VOLTAGE.getStartingDataAddress()) {
			int value = rover.getLightControlVoltage();
			return read.createResponse(new int[] { value });
		} else if (startingAddress == RoverModbusSlaveRead.LED_LOAD_CURRENT_SETTING.getStartingDataAddress()) {
			int value = rover.getLEDLoadCurrentSettingRaw();
			return read.createResponse(new int[] { value });
		} else if (startingAddress == RoverModbusSlaveRead.SPECIAL_POWER_CONTROL_E021.getStartingDataAddress()) {
			int value = rover.getSpecialPowerControlE021Raw();
			return read.createResponse(new int[] { value });
		} else if (startingAddress == Rover.Sensing.SENSING_1.getWorkingHoursRegister()) {
			Integer value = rover.getWorkingHoursRaw(Rover.Sensing.SENSING_1);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == Rover.Sensing.SENSING_2.getWorkingHoursRegister()) {
			Integer value = rover.getWorkingHoursRaw(Rover.Sensing.SENSING_2);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == Rover.Sensing.SENSING_3.getWorkingHoursRegister()) {
			Integer value = rover.getWorkingHoursRaw(Rover.Sensing.SENSING_3);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == Rover.Sensing.SENSING_1.getPowerWithPeopleSensedRegister()) {
			Integer value = rover.getPowerWithPeopleSensedRaw(Rover.Sensing.SENSING_1);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == Rover.Sensing.SENSING_2.getPowerWithPeopleSensedRegister()) {
			Integer value = rover.getPowerWithPeopleSensedRaw(Rover.Sensing.SENSING_2);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == Rover.Sensing.SENSING_3.getPowerWithPeopleSensedRegister()) {
			Integer value = rover.getPowerWithPeopleSensedRaw(Rover.Sensing.SENSING_3);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == Rover.Sensing.SENSING_1.getPowerWithNoPeopleSensedRegister()) {
			Integer value = rover.getPowerWithNoPeopleSensedRaw(Rover.Sensing.SENSING_1);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == Rover.Sensing.SENSING_2.getPowerWithNoPeopleSensedRegister()) {
			Integer value = rover.getPowerWithNoPeopleSensedRaw(Rover.Sensing.SENSING_2);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == Rover.Sensing.SENSING_3.getPowerWithNoPeopleSensedRegister()) {
			Integer value = rover.getPowerWithNoPeopleSensedRaw(Rover.Sensing.SENSING_3);
			if (value != null) {
				return read.createResponse(new int[] { value });
			}
		} else if (startingAddress == RoverModbusSlaveRead.SENSING_TIME_DELAY.getStartingDataAddress()) {
			Integer value = rover.getSensingTimeDelayRaw();
			if (value != null) {
				return read.createResponse(new int[]{ value });
			}
		} else if (startingAddress == RoverModbusSlaveRead.LED_LOAD_CURRENT.getStartingDataAddress()) {
			Integer value = rover.getLEDLoadCurrentRaw();
			if (value != null) {
				return read.createResponse(new int[]{ value });
			}
		} else if (startingAddress == RoverModbusSlaveRead.SPECIAL_POWER_CONTROL_E02D.getStartingDataAddress()) {
			Integer value = rover.getSpecialPowerControlE02DRaw();
			if (value != null) {
				return read.createResponse(new int[]{ value });
			}
		} else if (startingAddress == RoverModbusSlaveRead.CONTROLLER_CHARGING_POWER_SETTING.getStartingDataAddress()) {
			Integer value = rover.getControllerChargingPowerSetting();
			if (value != null) {
				return read.createResponse(new int[]{ value });
			}
		} else if (startingAddress == RoverModbusSlaveRead.GENERATOR_CHARGING_POWER_SETTING.getStartingDataAddress()) {
			Integer value = rover.getGeneratorChargingPowerSetting();
			if (value != null) {
				return read.createResponse(new int[]{ value });
			}
		}

		System.out.println("Unsupported: starting: " + read.getStartingDataAddress() + " number: " + read.getNumberOfRegisters());

		return ModbusMessages.createExceptionMessage(FunctionCode.READ_HOLDING_REGISTERS, RoverModbusSlaveRead.READ_EXCEPTION_UNSUPPORTED_REGISTER);
	}
}
