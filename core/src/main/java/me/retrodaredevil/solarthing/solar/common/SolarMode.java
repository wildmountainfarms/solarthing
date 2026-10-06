package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.Mode;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SolarMode extends Mode {
	SolarModeType getSolarModeType();
}
