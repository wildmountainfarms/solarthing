package me.retrodaredevil.solarthing.misc.weather;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifier;
import me.retrodaredevil.solarthing.misc.source.DeviceSource;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

import static java.util.Objects.requireNonNull;

@JsonIgnoreProperties(value = {"temperatureFahrenheit"}, allowGetters = true)
public class CelsiusTemperaturePacket implements TemperaturePacket {
	private final int dataId;
	private final DeviceSource deviceSource;
	private final float temperatureCelsius;

	private final DataIdentifier identifier;
	private final TemperatureIdentityInfo identityInfo;

	@JsonCreator
	public CelsiusTemperaturePacket(
			@JsonProperty(value = "dataId", required = true) int dataId,
			@JsonProperty(value = "source", required = true) DeviceSource deviceSource,
			@JsonProperty(value = "temperatureCelsius", required = true) float temperatureCelsius
	) {
		this.dataId = dataId;
		requireNonNull(this.deviceSource = deviceSource);
		this.temperatureCelsius = temperatureCelsius;

		identifier = new DataIdentifier(dataId);
		identityInfo = new TemperatureIdentityInfo(dataId);
	}

	@Override
	public float getTemperatureCelsius() {
		return temperatureCelsius;
	}

	@Override
	public float getTemperatureFahrenheit() {
		return temperatureCelsius * 1.8f + 32;
	}

	@Override
	public @NotNull DataIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getDataId() {
		return dataId;
	}

	@Override
	public DeviceSource getDeviceSource() {
		return deviceSource;
	}
}
