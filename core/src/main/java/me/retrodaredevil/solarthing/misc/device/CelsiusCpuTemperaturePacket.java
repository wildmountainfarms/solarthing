package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.SingleTypeIdentifier;

import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonIgnoreProperties(value = {"cpuTemperatureFahrenheit"}, allowGetters = true)
public class CelsiusCpuTemperaturePacket implements CpuTemperaturePacket {
	private final float cpuTemperatureCelsius;
	private final Identifier identifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	public CelsiusCpuTemperaturePacket(
			@JsonProperty(value = "cpuTemperatureCelsius", required = true) float cpuTemperatureCelsius
	) {
		this.cpuTemperatureCelsius = cpuTemperatureCelsius;
		identifier = new SingleTypeIdentifier(DevicePacketType.DEVICE_CPU_TEMPERATURE.toString());
		identityInfo = new DeviceIdentityInfo();
	}

	@Override
	public float getCpuTemperatureCelsius() {
		return cpuTemperatureCelsius;
	}

	@Override
	public float getCpuTemperatureFahrenheit() {
		return cpuTemperatureCelsius * 1.8f + 32;
	}

	@Override
	public @NotNull Identifier getIdentifier() {
		return identifier;
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}
}
