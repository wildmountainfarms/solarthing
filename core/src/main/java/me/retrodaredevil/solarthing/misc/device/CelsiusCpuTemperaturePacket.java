package me.retrodaredevil.solarthing.misc.device;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.SingleTypeIdentifier;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(value = {"cpuTemperatureFahrenheit"}, allowGetters = true)
public class CelsiusCpuTemperaturePacket implements CpuTemperaturePacket {
	private final Integer packetVersion;
	private final float cpuTemperatureCelsius;
	private final List<Core> cores;
	private final Identifier identifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	public CelsiusCpuTemperaturePacket(
			@JsonProperty(value = "packetVersion") Integer packetVersion,
			@JsonProperty(value = "cpuTemperatureCelsius", required = true) float cpuTemperatureCelsius,
			@JsonProperty("cores") List<Core> cores
	) {
		this.packetVersion = packetVersion;
		this.cpuTemperatureCelsius = cpuTemperatureCelsius;
		this.cores = cores == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(cores));
		identifier = new SingleTypeIdentifier(DevicePacketType.DEVICE_CPU_TEMPERATURE.toString());
		identityInfo = new DeviceIdentityInfo();

		if (packetVersion == null && !this.cores.isEmpty()) {
			throw new IllegalArgumentException("With a packetVersion of null, you should only be passing null or an empty list to cores! You passed: " + this.cores);
		}
	}

	@Override
	public @Nullable Integer getPacketVersion() {
		return packetVersion;
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
	public @NotNull List<Core> getCores() {
		return cores;
	}

	@Override
	public @NotNull Identifier getIdentifier() {
		return identifier;
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@JsonIgnoreProperties(value = {"temperatureFahrenheit"}, allowGetters = true)
	@JsonExplicit
	public static final class CelsiusCore implements Core {

		private final int number;
		private final float temperatureCelsius;

		@JsonCreator
		public CelsiusCore(
				@JsonProperty(value = "number", required = true) int number,
				@JsonProperty(value = "temperatureCelsius", required = true) float temperatureCelsius
		) {
			this.number = number;
			this.temperatureCelsius = temperatureCelsius;
		}

		@Override
		public int getNumber() {
			return number;
		}

		@Override
		public float getTemperatureCelsius() {
			return temperatureCelsius;
		}

		@Override
		public float getTemperatureFahrenheit() {
			return temperatureCelsius * 1.8f + 32;
		}
	}
}
