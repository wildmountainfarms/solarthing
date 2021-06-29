package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Mode;

public interface SolarMode extends Mode {
	@NotNull SolarModeType getSolarModeType();
}
