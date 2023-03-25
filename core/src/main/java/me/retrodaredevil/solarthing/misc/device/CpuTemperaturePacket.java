package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.PacketWithVersion;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.List;

@JsonDeserialize(as = CelsiusCpuTemperaturePacket.class)
@JsonExplicit
@JsonTypeName("DEVICE_CPU_TEMPERATURE")
public interface CpuTemperaturePacket extends DevicePacket, Identifiable, PacketWithVersion {
	Integer VERSION_ORIGINAL = null;
	int VERSION_WITH_CORES = 1;

	int VERSION_LATEST = VERSION_WITH_CORES;

	@DefaultFinal
	@Override
	default @NotNull DevicePacketType getPacketType(){
		return DevicePacketType.DEVICE_CPU_TEMPERATURE;
	}
	@JsonProperty("cpuTemperatureCelsius")
	float getCpuTemperatureCelsius();
	@JsonProperty("cpuTemperatureFahrenheit")
	float getCpuTemperatureFahrenheit();

	/**
	 * @return The list of cores and their temperatures or an empty list if {@link #getPacketVersion()} == {@link #VERSION_ORIGINAL} (null)
	 */
	@JsonProperty("cores")
	@NotNull List<Core> getCores();


	@JsonDeserialize(as = CelsiusCpuTemperaturePacket.CelsiusCore.class)
	@JsonExplicit
	interface Core {
		@JsonProperty("number")
		int getNumber();

		@JsonProperty("temperatureCelsius")
		float getTemperatureCelsius();
		@JsonProperty("temperatureFahrenheit")
		float getTemperatureFahrenheit();
	}
}
