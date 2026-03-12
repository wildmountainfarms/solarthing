package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SolarDevice extends Identifiable {
	// TODO remove NonNull
	@NonNull SolarMode getSolarMode();

	// TODO remove NonNull
	@GraphQLInclude("solarModeName")
	default @NonNull String getSolarModeName() {
		return getSolarMode().getModeName();
	}
	// TODO remove NonNull
	@GraphQLInclude("solarModeType")
	default @NonNull SolarModeType getSolarModeType() {
		return getSolarMode().getSolarModeType();
	}
	// TODO remove NonNull
	@GraphQLInclude("solarModeTypeDisplayName")
	default @NonNull String getSolarModeTypeDisplayName() {
		return getSolarModeType().getModeName();
	}
}
