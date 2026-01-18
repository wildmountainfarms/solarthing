package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class RoverStatusPackets {
	private RoverStatusPackets(){ throw new UnsupportedOperationException(); }

	public static RoverStatusPacket createFromReadTable(int number, RoverReadTable read){
		// it has to be read 76 different times
		return new ImmutableRoverStatusPacket(
				RoverStatusPacket.Version.LATEST,
				number,
				read.getMaxVoltageValue(), read.getRatedChargingCurrentValue(),
				read.getRatedDischargingCurrentValue(), read.getProductTypeValue(),
				read.getProductModelValue(),
				read.getSoftwareVersionValue(), read.getHardwareVersionValue(),
				read.getProductSerialNumber(), read.getControllerDeviceAddress(),
				read.getBatteryCapacitySOC(), read.getBatteryVoltage(),
				read.getChargingCurrent(), read.getControllerTemperatureRaw(), read.getBatteryTemperatureRaw(),
				read.getLoadVoltageRaw(), read.getLoadCurrentRaw(),
				read.getLoadPowerRaw(), read.getPVVoltage(), read.getPVCurrent(),
				read.getChargingPower(), read.getDailyMinBatteryVoltage(), read.getDailyMaxBatteryVoltage(),
				read.getDailyMaxChargingCurrent(), read.getDailyMaxDischargingCurrent(),
				read.getDailyMaxChargingPower(), read.getDailyMaxDischargingPower(),
				read.getDailyAH(), read.getDailyAHDischarging(),
				read.getDailyKWH(), read.getDailyKWHConsumption(),
				read.getOperatingDaysCount(),
				read.getBatteryOverDischargesCount(), read.getBatteryFullChargesCount(),
				read.getChargingAmpHoursOfBatteryCount(), read.getDischargingAmpHoursOfBatteryCount(),
				read.getCumulativeKWH(), read.getCumulativeKWHConsumption(),
				read.getRawStreetLightValue(), read.getChargingStateValue(),
				read.getErrorModeValue(), read.getNominalBatteryCapacity(),
				read.getSystemVoltageSettingValue(), read.getRecognizedVoltageValue(),
				read.getBatteryTypeValue(),
				read.getOverVoltageThresholdRaw(), read.getChargingVoltageLimitRaw(),
				read.getEqualizingChargingVoltageRaw(), read.getBoostChargingVoltageRaw(),
				read.getFloatingChargingVoltageRaw(), read.getBoostChargingRecoveryVoltageRaw(),
				read.getOverDischargeRecoveryVoltageRaw(), read.getUnderVoltageWarningLevelRaw(),
				read.getOverDischargeVoltageRaw(), read.getDischargingLimitVoltageRaw(),
				read.getEndOfChargeSOC(), read.getEndOfDischargeSOC(),
				read.getOverDischargeTimeDelaySeconds(),
				read.getEqualizingChargingTimeRaw(), read.getBoostChargingTimeRaw(),
				read.getEqualizingChargingIntervalRaw(), read.getTemperatureCompensationFactorRaw(),
				read.getOperatingSettingBundle(Rover.OperatingSetting.STAGE_1), read.getOperatingSettingBundle(Rover.OperatingSetting.STAGE_2),
				read.getOperatingSettingBundle(Rover.OperatingSetting.STAGE_3), read.getOperatingSettingBundle(Rover.OperatingSetting.MORNING_ON),
				read.getLoadWorkingModeValue(),
				read.getLightControlDelayMinutes(), read.getLightControlVoltage(),
				read.getLEDLoadCurrentSettingRaw(),
				read.getSpecialPowerControlE021Raw(),
				read.getSensed1(), read.getSensed2(), read.getSensed3(),
				null, read.getLEDLoadCurrentRaw(),
				read.getSpecialPowerControlE02DRaw()
		);
	}
}
