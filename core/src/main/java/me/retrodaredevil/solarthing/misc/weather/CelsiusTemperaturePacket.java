package me.retrodaredevil.solarthing.misc.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

@JsonIgnoreProperties(value = {"temperatureFahrenheit"}, allowGetters = true)
public class CelsiusTemperaturePacket implements TemperaturePacket {
	private final int dataId;
	private final float temperatureCelsius;

	private final DataIdentifier identifier;

	public CelsiusTemperaturePacket(
			@JsonProperty(value = "dataId", required = true) int dataId,
			@JsonProperty(value = "temperatureCelsius", required = true) float temperatureCelsius
	) {
		this.dataId = dataId;
		this.temperatureCelsius = temperatureCelsius;

		identifier = new DataIdentifier(dataId);
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
		return null;
	}

	@Override
	public int getDataId() {
		return dataId;
	}
}
