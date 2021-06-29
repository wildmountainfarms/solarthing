package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.solar.common.SolarMode;
import me.retrodaredevil.solarthing.solar.common.SolarModeType;

/**
 * Charging status represents the current mode of the charge controller. However, while in any of these modes, (except {@link #NO_CHARGING},
 * the controller may actually be in Bulk mode.
 */
public enum ChargingStatus implements CodeMode, SolarMode {
	NO_CHARGING(0, "No charging", SolarModeType.CHARGE_CONTROLLER_OFF),
	FLOAT(1, "Float", SolarModeType.BULK_FLOAT),
	BOOST(2, "Boost", SolarModeType.BULK_ABSORB),
	EQUALIZATION(3, "Equalization", SolarModeType.BULK_EQUALIZE),
	;
	private final int value;
	private final String name;
	private final SolarModeType solarModeType;

	ChargingStatus(int value, String name, SolarModeType solarModeType) {
		this.value = value;
		this.name = name;
		this.solarModeType = solarModeType;
	}

	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public String getModeName() {
		return name;
	}

	@Override
	public @NotNull SolarModeType getSolarModeType() {
		return solarModeType;
	}
}
