package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.Mode;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SolarMode extends Mode {
	// TODO remove NonNull
	@NonNull SolarModeType getSolarModeType();
}
