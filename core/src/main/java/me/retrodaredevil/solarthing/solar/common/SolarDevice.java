package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NonNull;

public interface SolarDevice extends Identifiable {
	@NonNull SolarMode getSolarMode();

	@GraphQLInclude("solarModeName")
	default @NonNull String getSolarModeName() {
		return getSolarMode().getModeName();
	}
	@GraphQLInclude("solarModeType")
	default @NonNull SolarModeType getSolarModeType() {
		return getSolarMode().getSolarModeType();
	}
	@GraphQLInclude("solarModeTypeDisplayName")
	default @NonNull String getSolarModeTypeDisplayName() {
		return getSolarModeType().getModeName();
	}
}
