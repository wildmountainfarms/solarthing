package me.retrodaredevil.solarthing.solar.tracer.batteryconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.tracer.mode.TracerBatteryType;

public class TracerBatteryConfigBuilder implements TracerBatteryConfig {
	private TracerBatteryType tracerBatteryType;
	private int batteryCapacityAmpHours;
	private int temperatureCompensationCoefficient;
	private float highVoltageDisconnect, chargingLimitVoltage, overVoltageReconnect, equalizationVoltage, boostVoltage, floatVoltage;
	private float boostReconnectVoltage, lowVoltageReconnect, underVoltageRecover, underVoltageWarning, lowVoltageDisconnect, dischargingLimitVoltage;

	public TracerBatteryConfigBuilder(TracerBatteryConfig base) {
		tracerBatteryType = base.getBatteryType();
		batteryCapacityAmpHours = base.getBatteryCapacityAmpHours();
		temperatureCompensationCoefficient = base.getTemperatureCompensationCoefficient();
		highVoltageDisconnect = base.getHighVoltageDisconnect();
		chargingLimitVoltage = base.getChargingLimitVoltage();
		overVoltageReconnect = base.getOverVoltageReconnect();
		equalizationVoltage = base.getEqualizationVoltage();
		boostReconnectVoltage = base.getBoostReconnectVoltage();
		floatVoltage = base.getFloatVoltage();
		boostReconnectVoltage = base.getBoostReconnectVoltage();
		lowVoltageReconnect = base.getLowVoltageReconnect();
		underVoltageRecover = base.getUnderVoltageRecover();
		underVoltageWarning = base.getUnderVoltageWarning();
		lowVoltageDisconnect = base.getLowVoltageDisconnect();
		dischargingLimitVoltage = base.getDischargingLimitVoltage();
	}


	@Override public @NotNull TracerBatteryType getBatteryType() { return tracerBatteryType; }
	@JsonProperty("batteryType")
	public void setTracerBatteryType(TracerBatteryType tracerBatteryType) {
		this.tracerBatteryType = tracerBatteryType;
	}

	@Override public int getBatteryCapacityAmpHours() { return batteryCapacityAmpHours; }
	@JsonProperty("batteryCapacityAmpHours")
	public void setBatteryCapacityAmpHours(int batteryCapacityAmpHours) {
		this.batteryCapacityAmpHours = batteryCapacityAmpHours;
	}

	@Override public int getTemperatureCompensationCoefficient() { return temperatureCompensationCoefficient; }
	@JsonProperty("temperatureCompensationCoefficient")
	public void setTemperatureCompensationCoefficient(int temperatureCompensationCoefficient) {
		this.temperatureCompensationCoefficient = temperatureCompensationCoefficient;
	}

	@Override public float getHighVoltageDisconnect() { return highVoltageDisconnect; }
	@JsonProperty("highVoltageDisconnect")
	public void setHighVoltageDisconnect(float highVoltageDisconnect) {
		this.highVoltageDisconnect = highVoltageDisconnect;
	}

	@Override public float getChargingLimitVoltage() { return chargingLimitVoltage; }
	@JsonProperty("chargingLimitVoltage")
	public void setChargingLimitVoltage(float chargingLimitVoltage) {
		this.chargingLimitVoltage = chargingLimitVoltage;
	}

	@Override public float getOverVoltageReconnect() { return overVoltageReconnect; }
	@JsonProperty("overVoltageReconnect")
	public void setOverVoltageReconnect(float overVoltageReconnect) {
		this.overVoltageReconnect = overVoltageReconnect;
	}

	@Override public float getEqualizationVoltage() { return equalizationVoltage; }
	@JsonProperty("equalizationVoltage")
	public void setEqualizationVoltage(float equalizationVoltage) {
		this.equalizationVoltage = equalizationVoltage;
	}

	@Override public float getBoostVoltage() { return boostVoltage; }
	@JsonProperty("boostVoltage")
	public void setBoostVoltage(float boostVoltage) {
		this.boostVoltage = boostVoltage;
	}

	@Override public float getFloatVoltage() { return floatVoltage; }
	@JsonProperty("floatVoltage")
	public void setFloatVoltage(float floatVoltage) {
		this.floatVoltage = floatVoltage;
	}

	@Override public float getBoostReconnectVoltage() { return boostReconnectVoltage; }
	@JsonProperty("boostReconnectVoltage")
	public void setBoostReconnectVoltage(float boostReconnectVoltage) {
		this.boostReconnectVoltage = boostReconnectVoltage;
	}

	@Override public float getLowVoltageReconnect() { return lowVoltageReconnect; }
	@JsonProperty("lowVoltageReconnect")
	public void setLowVoltageReconnect(float lowVoltageReconnect) {
		this.lowVoltageReconnect = lowVoltageReconnect;
	}

	@Override public float getUnderVoltageRecover() { return underVoltageRecover; }
	@JsonProperty("underVoltageRecover")
	public void setUnderVoltageRecover(float underVoltageRecover) {
		this.underVoltageRecover = underVoltageRecover;
	}

	@Override public float getUnderVoltageWarning() { return underVoltageWarning; }
	@JsonProperty("underVoltageWarning")
	public void setUnderVoltageWarning(float underVoltageWarning) {
		this.underVoltageWarning = underVoltageWarning;
	}

	@Override public float getLowVoltageDisconnect() { return lowVoltageDisconnect; }
	@JsonProperty("lowVoltageDisconnect")
	public void setLowVoltageDisconnect(float lowVoltageDisconnect) {
		this.lowVoltageDisconnect = lowVoltageDisconnect;
	}

	@Override public float getDischargingLimitVoltage() { return dischargingLimitVoltage; }
	@JsonProperty("dischargingLimitVoltage")
	public void setDischargingLimitVoltage(float dischargingLimitVoltage) {
		this.dischargingLimitVoltage = dischargingLimitVoltage;
	}
}
