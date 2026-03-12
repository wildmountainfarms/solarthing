package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DualTemperature extends BatteryTemperature, ControllerTemperature, Identifiable {
}
