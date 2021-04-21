package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.common.*;

public interface TracerReadTable extends RecordBatteryVoltage, BasicChargeController, AdvancedAccumulatedChargeController {

	float getRatedInputVoltage();
	float getRatedInputCurrent();
	float getRatedInputPower();


	float getRatedOutputVoltage(); // basically the nominal battery voltage (I think)
	float getRatedOutputCurrent();
	float getRatedOutputPower();

	int getChargingModeValue();

	float getRatedLoadOutputCurrent();

	// ===

	@Override
	@NotNull Float getPVVoltage(); // 0x3100
	@Override
	@NotNull Float getPVCurrent();
	@Override
	@NotNull Float getPVWattage();

	@Override
	float getBatteryVoltage(); // 0x3104

	@Override
	@NotNull Float getChargingCurrent();
	// Page 2
	@Override
	@NotNull Float getChargingPower();

	float getLoadVoltage();
	float getLoadCurrent();
	float getLoadPower();

	float getBatteryTemperatureCelsius(); // 0x3110
	float getInsideControllerTemperatureCelsius();
	float getPowerComponentTemperatureCelsius();

	int getBatterySOC(); // TODO Is the raw range of this 0 to 100? Or 0 to (100 * 100)? If 0 to (100 * 100), then we should make this a double, maybe make it a double anyway

	float getRemoteBatteryTemperatureCelsius();

	int getRealBatteryRatedVoltageValue();

	// ===

	int getBatteryStatusValue();
	int getChargingEquipmentStatus();

	// Page 3
	// region Read Only Accumulators + Extra
	float getDailyMaxInputVoltage();
	float getDailyMinInputVoltage();
	@Override
	float getDailyMaxBatteryVoltage(); // 0x3302
	@Override
	float getDailyMinBatteryVoltage();
	@Override
	float getDailyKWHConsumption();
	float getMonthlyKWHConsumption();
	float getYearlyKWHConsumption();
	@Override
	float getCumulativeKWHConsumption();
	@Override
	float getDailyKWH(); // 0x330C
	float getMonthlyKWH();
	float getYearlyKWH();
	@Override
	float getCumulativeKWH();
	float getCarbonDioxideReductionTons();

	float getNetBatteryCurrent();
	float getBatteryTemperatureCelsius331D(); // 0x331D
	float getAmbientTemperatureCelsius();
	// endregion

	// region Read-write settings
	int getBatteryTypeValue(); // 0x9000
	int getBatteryCapacityAmpHours();
	int getTemperatureCompensationCoefficient();
	float getHighVoltageDisconnect();
	float getChargingLimitVoltage();
	float getOverVoltageReconnect();
	float getEqualizationVoltage();
	float getBoostVoltage();
	float getFloatVoltage();
	float getBoostReconnectVoltage();
	float getLowVoltageReconnect();
	float getUnderVoltageRecover();
	float getUnderVoltageWarning();
	float getLowVoltageDisconnect();
	float getDischargingLimitVoltage();

	// Minutes and Seconds on 0x9013
	// Hour and Day in 0x9014
	// Month and Year in 0x9015
	int getEqualizationChargingCycleDays();
	float getBatteryTemperatureWarningUpperLimit();
	float getBatteryTemperatureWarningLowerLimit();
	float getInsideControllerTemperatureWarningUpperLimit();
	float getInsideControllerTemperatureWarningUpperLimitRecover();
	float getPowerComponentTemperatureUpperLimit();
	float getPowerComponentTemperatureUpperLimitRecover();
	float getLineImpedance(); // 0x901D // milliohms
	float getNightPVVoltageThreshold();
	int getLightSignalStartupDelayTime();
	float getDayPVVoltageThreshold();
	int getLightSignalTurnOffDelayTime();
	int getLoadControlModeValue();
	// Working time length 1
	// Working time length 2
	// Turn on timing 1
	// Turn off timing 1
	// Turn on timing 2
	// Turn off timing 2
	// Length of night 0x9065
	int getBatteryRatedVoltageCode();
	int getLoadTimingControlSelectionValueRaw();
	boolean isLoadOnByDefaultInManualMode(); // 0x906A
	int getEqualizeDuration();
	int getBoostDuration();
	int getDischargingPercentage();
	int getChargingPercentage();
	// management modes of battery charging and discharging

	boolean isManualLoadControlOn();
	boolean isLoadTestModeEnabled();
	boolean isLoadForcedOn();
	boolean isInsideControllerOverTemperature();
	boolean isNight();

	// endregion

}
