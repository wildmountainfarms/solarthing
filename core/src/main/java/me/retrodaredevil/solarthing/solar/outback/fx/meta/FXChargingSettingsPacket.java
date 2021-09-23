package me.retrodaredevil.solarthing.solar.outback.fx.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.type.closed.meta.TargetedMetaPacket;
import me.retrodaredevil.solarthing.type.closed.meta.TargetedMetaPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingSettings;

@JsonExplicit
@JsonTypeName("FX_CHARGING_SETTINGS")
public class FXChargingSettingsPacket implements TargetedMetaPacket {
	private final FXChargingSettings fxChargingSettings;
	private final int temperatureAdjustCelsius;

	@JsonCreator
	public FXChargingSettingsPacket(
			@JsonProperty(value = "settings", required = true) Settings settings,
			@JsonProperty("temperatureAdjustCelsius") Integer temperatureAdjustCelsius) {
		this.fxChargingSettings = new FXChargingSettings(
				settings.rebulkVoltage, settings.absorbVoltage, Math.round(settings.absorbTimeHours * 60 * 60 * 1000),
				settings.floatVoltage, Math.round(settings.floatTimeHours * 60 * 60 * 1000),
				settings.refloatVoltage, settings.equalizeVoltage, Math.round(settings.equalizeTimeHours * 60 * 60 * 1000)
		);
		this.temperatureAdjustCelsius = temperatureAdjustCelsius == null ? 0 : temperatureAdjustCelsius;
	}

	@Override
	public @NotNull TargetedMetaPacketType getPacketType() {
		return TargetedMetaPacketType.FX_CHARGING_SETTINGS;
	}

	public FXChargingSettings getFXChargingSettings() {
		return fxChargingSettings;
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


	public static class Settings {
		private final Float rebulkVoltage;

		private final float absorbVoltage;
		private final double absorbTimeHours;

		private final float floatVoltage;
		private final double floatTimeHours;
		private final float refloatVoltage;

		private final float equalizeVoltage;
		private final double equalizeTimeHours;

		@JsonCreator
		public Settings(
				@JsonProperty("rebulkVoltage") Float rebulkVoltage,
				@JsonProperty(value = "absorbVoltage", required = true) float absorbVoltage,
				@JsonProperty(value = "absorbTimeHours", required = true) double absorbTimeHours,
				@JsonProperty(value = "floatVoltage", required = true) float floatVoltage,
				@JsonProperty(value = "floatTimeHours", required = true) double floatTimeHours,
				@JsonProperty(value = "refloatVoltage", required = true) float refloatVoltage,
				@JsonProperty(value = "equalizeVoltage", required = true) float equalizeVoltage,
				@JsonProperty(value = "equalizeTimeHours", required = true) double equalizeTimeHours) {
			this.rebulkVoltage = rebulkVoltage;
			this.absorbVoltage = absorbVoltage;
			this.absorbTimeHours = absorbTimeHours;
			this.floatVoltage = floatVoltage;
			this.floatTimeHours = floatTimeHours;
			this.refloatVoltage = refloatVoltage;
			this.equalizeVoltage = equalizeVoltage;
			this.equalizeTimeHours = equalizeTimeHours;
		}
	}
}
