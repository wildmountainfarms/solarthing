package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public class TracerStatusPackets {
	private TracerStatusPackets() { throw new UnsupportedOperationException(); }

	public static TracerStatusPacket createFromReadTable(int number, TracerReadTable read) {
		return new ImmutableTracerStatusPacket(
				TracerStatusPacket.CHARGING_EQUIPMENT_FIX_VERSION, number,
				read.getRatedInputVoltage(), read.getRatedInputCurrent(), read.getRatedInputPower(),
				read.getRatedOutputVoltage(), read.getRatedOutputCurrent(), read.getRatedOutputPower(),
				read.getChargingTypeValue(), read.getRatedLoadOutputCurrent(),
				read.getPVVoltage(), read.getPVCurrent(), read.getPVWattage(),
				read.getBatteryVoltage(), read.getChargingCurrent(), read.getChargingPower(),
				read.getLoadVoltage(), read.getLoadCurrent(), read.getLoadPower(),
				read.getBatteryTemperatureCelsius(), read.getInsideControllerTemperatureCelsius(), read.getPowerComponentTemperatureCelsius(),
				read.getBatterySOC(),
				read.getRemoteBatteryTemperatureCelsius(),
				read.getRealBatteryRatedVoltageValue(), read.getBatteryStatusValue(), read.getChargingEquipmentStatus(),
				read.getDailyMaxPVVoltage(), read.getDailyMinPVVoltage(),
				read.getDailyMaxBatteryVoltage(), read.getDailyMinBatteryVoltage(),
				read.getDailyKWHConsumption(), read.getMonthlyKWHConsumption(), read.getYearlyKWHConsumption(), read.getCumulativeKWHConsumption(),
				read.getDailyKWH(), read.getMonthlyKWH(), read.getYearlyKWH(), read.getCumulativeKWH(),
				read.getCarbonDioxideReductionTons(),
				read.getNetBatteryCurrent(),
				read.getBatteryTemperatureCelsius331D(), read.getAmbientTemperatureCelsius(),
				read.getBatteryTypeValue(), read.getBatteryCapacityAmpHours(), read.getTemperatureCompensationCoefficient(),
				read.getHighVoltageDisconnect(), read.getChargingLimitVoltage(), read.getOverVoltageReconnect(),
				read.getEqualizationVoltage(), read.getBoostVoltage(), read.getFloatVoltage(),
				read.getBoostReconnectVoltage(), read.getLowVoltageReconnect(), read.getUnderVoltageRecover(), read.getUnderVoltageWarning(),
				read.getLowVoltageDisconnect(), read.getDischargingLimitVoltage(),
				read.getSecondMinuteHourDayMonthYearRaw(), read.getEqualizationChargingCycleDays(),
				read.getBatteryTemperatureWarningUpperLimit(), read.getBatteryTemperatureWarningLowerLimit(),
				read.getInsideControllerTemperatureWarningUpperLimit(), read.getInsideControllerTemperatureWarningUpperLimitRecover(),
				read.getPowerComponentTemperatureUpperLimit(), read.getPowerComponentTemperatureUpperLimitRecover(),
				read.getLineImpedance(),
				read.getNightPVVoltageThreshold(), read.getLightSignalStartupDelayTime(),
				read.getDayPVVoltageThreshold(), read.getLightSignalTurnOffDelayTime(),
				read.getLoadControlModeValue(),
				read.getWorkingTimeLength1Raw(), read.getWorkingTimeLength2Raw(),
				read.getTurnOnTiming1Raw(), read.getTurnOffTiming1Raw(),
				read.getTurnOnTiming2Raw(), read.getTurnOffTiming2Raw(),
				read.getLengthOfNightRaw(),
				read.getBatteryRatedVoltageCode(), read.getLoadTimingControlSelectionValue(),
				read.isLoadOnByDefaultInManualMode(),
				read.getEqualizeDurationMinutes(), read.getBoostDurationMinutes(),
				read.getDischargingPercentage(), read.getChargingPercentage(),
				read.getBatteryManagementModeValue(),
				read.isManualLoadControlOn(), read.isLoadTestModeEnabled(), read.isLoadForcedOn(),
				read.isInsideControllerOverTemperature(), read.isNight()
		);
	}
}
