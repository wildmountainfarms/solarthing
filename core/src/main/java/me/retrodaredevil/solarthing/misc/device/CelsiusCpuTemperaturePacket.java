package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CelsiusCpuTemperaturePacket implements CpuTemperaturePacket {
	private final float cpuTemperatureCelsius;

	@JsonCreator
	@JsonIgnoreProperties({"cpuTemperatureFahrenheit"})
	public CelsiusCpuTemperaturePacket(
			@JsonProperty("cpuTemperatureCelsius") float cpuTemperatureCelsius
	) {
		this.cpuTemperatureCelsius = cpuTemperatureCelsius;
	}

	@Override
	public float getCpuTemperatureCelsius() {
		return cpuTemperatureCelsius;
	}

	@Override
	public float getCpuTemperatureFahrenheit() {
		return cpuTemperatureCelsius * 1.8f + 32;
	}
}
