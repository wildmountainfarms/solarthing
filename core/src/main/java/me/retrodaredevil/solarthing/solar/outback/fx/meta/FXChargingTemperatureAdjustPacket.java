package me.retrodaredevil.solarthing.solar.outback.fx.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.meta.TargetedMetaPacket;
import me.retrodaredevil.solarthing.meta.TargetedMetaPacketType;

/**
 * Used to adjust the temperature of the sensor used to get the battery's temperature. This is only used if the temperature
 * used to get the battery's temperature is different than the one being used (which is has to be since getting the
 * Outback RTS value is not possible with the Mate1/2 over serial)
 */
@JsonExplicit
@JsonTypeName("FX_CHARGING_TEMPERATURE_ADJUST")
public class FXChargingTemperatureAdjustPacket implements TargetedMetaPacket {
	private final int temperatureAdjustCelsius;
	@JsonCreator
	public FXChargingTemperatureAdjustPacket(@JsonProperty(value = "temperatureAdjustCelsius", required = true) int temperatureAdjustCelsius) {
		this.temperatureAdjustCelsius = temperatureAdjustCelsius;
	}

	@Override
	public @NotNull TargetedMetaPacketType getPacketType() {
		return TargetedMetaPacketType.FX_CHARGING_TEMPERATURE_ADJUST;
	}

	/**
	 * If the outback sensor is directly being used or the sensor
	 * being used to read the temperature is the same as the outback sensor, this should be 0.
	 *
	 * @return The temperature in celsius to add to the reading of the sensor being used to measure the battery temperature. When this value is
	 * added to the read value, it should be close to the outback temperature sensor reading.
	 */
	public int getTemperatureAdjustCelsius() {
		return temperatureAdjustCelsius;
	}
}
