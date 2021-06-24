package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.Mode;

public interface BasicSolarMode extends Mode {
	@GraphQLInclude("isOn")
	boolean isOn();
}
