package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;

public interface SolarDevice {
	@NotNull SolarMode getSolarMode();

	@GraphQLInclude("solarModeName")
	default @NotNull String getSolarModeName() {
		return getSolarMode().getModeName();
	}
	@GraphQLInclude("solarModeType")
	default @NotNull SolarModeType getSolarModeType() {
		return getSolarMode().getSolarModeType();
	}
	@GraphQLInclude("solarModeTypeDisplayName")
	default @NotNull String getSolarModeTypeDisplayName() {
		return getSolarModeType().getModeName();
	}
}
