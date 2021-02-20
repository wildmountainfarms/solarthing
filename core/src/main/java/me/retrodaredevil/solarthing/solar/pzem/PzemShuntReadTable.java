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
	int getEnergyValueRaw();
	default int getEnergyWattHours() {
		return getEnergyValueRaw();
	}
	default float getEnergyKWH() {
		return getEnergyWattHours() / 1000.0f;
	}

	@JsonProperty("highVoltageAlarmStatus")
	int getHighVoltageAlarmStatus();
	@JsonProperty("lowVoltageAlarmStatus")
	int getLowVoltageAlarmStatus();

	default boolean isHighVoltageAlarm() { return getHighVoltageAlarmStatus() != 0; }
	default boolean isLowVoltageAlarm() { return getLowVoltageAlarmStatus() != 0; }

}
