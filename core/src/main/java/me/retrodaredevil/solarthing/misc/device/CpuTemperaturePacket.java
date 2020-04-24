package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

import javax.validation.constraints.NotNull;

@JsonDeserialize(as = CelsiusCpuTemperaturePacket.class)
@JsonExplicit
@JsonTypeName("DEVICE_CPU_TEMPERATURE")
public interface CpuTemperaturePacket extends DevicePacket, Identifiable {
	@NotNull
    @Override
	default DevicePacketType getPacketType(){
		return DevicePacketType.DEVICE_CPU_TEMPERATURE;
	}
	@JsonProperty("cpuTemperatureCelsius")
	float getCpuTemperatureCelsius();
	@JsonProperty("cpuTemperatureFahrenheit")
	float getCpuTemperatureFahrenheit();
}
