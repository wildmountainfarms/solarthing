package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SolarDevice extends Identifiable {
	SolarMode getSolarMode();

	@GraphQLInclude("solarModeName")
	default String getSolarModeName() {
		return getSolarMode().getModeName();
	}
	@GraphQLInclude("solarModeType")
	default SolarModeType getSolarModeType() {
		return getSolarMode().getSolarModeType();
	}
	@GraphQLInclude("solarModeTypeDisplayName")
	default String getSolarModeTypeDisplayName() {
		return getSolarModeType().getModeName();
	}
}
