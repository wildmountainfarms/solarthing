package me.retrodaredevil.solarthing.solar.outback.fx.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.outback.fx.MiscMode;

import me.retrodaredevil.solarthing.annotations.NotNull;
import java.util.Set;

public interface FXMiscReporter {
	int getMiscValue();
	@GraphQLInclude("miscModes")
	default @NotNull Set<@NotNull MiscMode> getMiscModes(){ return Modes.getActiveModes(MiscMode.class, getMiscValue()); }

	@GraphQLInclude("is230V")
	default boolean is230V() {
		return MiscMode.FX_230V_UNIT.isActive(getMiscValue());
	}
	@GraphQLInclude("isAuxOn")
	default boolean isAuxOn() {
		return MiscMode.AUX_OUTPUT_ON.isActive(getMiscValue());
	}

}
