package me.retrodaredevil.solarthing.solar.tracer.batteryconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.tracer.mode.TracerBatteryType;

public interface TracerBatteryConfig {
	@NotNull TracerBatteryType getBatteryType();
	@JsonProperty("batteryCapacityAmpHours")
	int getBatteryCapacityAmpHours();
	/** @return number in range 0-9 */
	@JsonProperty("temperatureCompensationCoefficient")
	int getTemperatureCompensationCoefficient();
	@JsonProperty("highVoltageDisconnect")
	float getHighVoltageDisconnect();
	@JsonProperty("chargingLimitVoltage")
	float getChargingLimitVoltage();
	@JsonProperty("overVoltageReconnect")
	float getOverVoltageReconnect();
	@JsonProperty("equalizationVoltage")
	float getEqualizationVoltage();
	@JsonProperty("boostVoltage")
	float getBoostVoltage();
	@JsonProperty("floatVoltage")
	float getFloatVoltage();
	@JsonProperty("boostReconnectVoltage")
	float getBoostReconnectVoltage();
	@JsonProperty("lowVoltageReconnect")
	float getLowVoltageReconnect();
	@JsonProperty("underVoltageRecover")
	float getUnderVoltageRecover();
	@JsonProperty("underVoltageWarning")
	float getUnderVoltageWarning();
	@JsonProperty("lowVoltageDisconnect")
	float getLowVoltageDisconnect();
	@JsonProperty("dischargingLimitVoltage")
	float getDischargingLimitVoltage();

}
