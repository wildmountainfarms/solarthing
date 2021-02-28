package me.retrodaredevil.solarthing.solar.pzem;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

@JsonExplicit
public interface PzemShuntReadTable {
	SerialConfig SERIAL_CONFIG = new SerialConfigBuilder(9600).setDataBits(8).setStopBits(SerialConfig.StopBits.TWO).build();

	@JsonProperty("voltageValueRaw")
	int getVoltageValueRaw();
	@GraphQLInclude("voltage")
	default float getVoltage() {
		return getVoltageValueRaw() / 100.0f;
	}

	@JsonProperty("currentValueRaw")
	int getCurrentValueRaw();
	@GraphQLInclude("currentAmps")
	default float getCurrentAmps() {
		return getCurrentValueRaw() / 100.0f;
	}

	@JsonProperty("powerValueRaw")
	int getPowerValueRaw();
	@GraphQLInclude("powerWatts")
	default float getPowerWatts() {
		return getPowerValueRaw() / 10.0f;
	}

	@JsonProperty("energyValueRaw")
	int getEnergyValueRaw();
	@GraphQLInclude("energyWattHours")
	default int getEnergyWattHours() {
		return getEnergyValueRaw();
	}
	@GraphQLInclude("energyKWH")
	default float getEnergyKWH() {
		return getEnergyWattHours() / 1000.0f;
	}

	@JsonProperty("highVoltageAlarmStatus")
	int getHighVoltageAlarmStatus();
	@JsonProperty("lowVoltageAlarmStatus")
	int getLowVoltageAlarmStatus();

	@GraphQLInclude("isHighVoltageAlarm")
	default boolean isHighVoltageAlarm() { return getHighVoltageAlarmStatus() != 0; }
	@GraphQLInclude("isLowVoltageAlarm")
	default boolean isLowVoltageAlarm() { return getLowVoltageAlarmStatus() != 0; }

}
