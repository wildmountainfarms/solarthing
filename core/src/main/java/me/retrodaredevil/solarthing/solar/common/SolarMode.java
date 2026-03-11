package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.Mode;
import org.jspecify.annotations.NonNull;

public interface SolarMode extends Mode {
	@NonNull SolarModeType getSolarModeType();
}
