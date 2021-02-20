package me.retrodaredevil.solarthing.solar.pzem;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.Nullable;

@JsonExplicit
public interface PzemShuntReadTable {
	SerialConfig SERIAL_CONFIG = new SerialConfigBuilder(9600).setDataBits(8).setStopBits(SerialConfig.StopBits.TWO).build();

	@JsonProperty("voltageValueRaw")
	int getVoltageValueRaw();
	default float getVoltage() {
		return getVoltageValueRaw() / 100.0f;
	}
	@JsonProperty("currentValueRaw")
	int getCurrentValueRaw();
	default float getCurrentAmps() {
		return getCurrentValueRaw() / 100.0f;
	}
	@JsonProperty("powerValueRaw")
	int getPowerValueRaw();
	default float getPowerWatts() {
		return getPowerValueRaw() / 10.0f;
	}

	@JsonProperty("energyValueRaw")
	@Nullable Integer getEnergyValueRaw();
	default @Nullable Integer getEnergyWattHours() {
		return getEnergyValueRaw();
	}
	default @Nullable Float getEnergyKWH() {
		Integer raw = getEnergyValueRaw();
		return raw == null ? null : raw / 1000.0f;
	}

	@JsonProperty("highVoltageAlarmStatus")
	@Nullable Integer getHighVoltageAlarmStatus();
	@JsonProperty("lowVoltageAlarmStatus")
	@Nullable Integer getLowVoltageAlarmStatus();

	default @Nullable Boolean isHighVoltageAlarm() {
		Integer value = getHighVoltageAlarmStatus();
		return value == null ? null : value != 0;
	}
	default @Nullable Boolean isLowVoltageAlarm() {
		Integer value = getLowVoltageAlarmStatus();
		return value == null ? null : value != 0;
	}

}
