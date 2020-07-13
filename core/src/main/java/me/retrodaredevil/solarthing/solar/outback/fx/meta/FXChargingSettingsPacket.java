package me.retrodaredevil.solarthing.solar.outback.fx.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.meta.TargetedMetaPacket;
import me.retrodaredevil.solarthing.meta.TargetedMetaPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingSettings;

public class FXChargingSettingsPacket implements TargetedMetaPacket {
	private final FXChargingSettings fxChargingSettings;

	@JsonCreator
	public FXChargingSettingsPacket(@JsonProperty(value = "settings", required = true) Settings settings) {
		this.fxChargingSettings = new FXChargingSettings(
				settings.rebulkVoltage, settings.absorbVoltage, Math.round(settings.absorbTimeHours * 60 * 1000),
				settings.floatVoltage, Math.round(settings.floatTimeHours * 60 * 1000),
				settings.refloatVoltage, settings.equalizeVoltage, Math.round(settings.equalizeTimeHours * 60 * 1000)
		);
	}

	@Override
	public @NotNull TargetedMetaPacketType getPacketType() {
		return TargetedMetaPacketType.FX_CHARGING_SETTINGS;
	}

	public FXChargingSettings getFXChargingSettings() {
		return fxChargingSettings;
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
