package me.retrodaredevil.solarthing.solar.outback.fx.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.outback.fx.MiscMode;

import javax.validation.constraints.NotNull;
import java.util.Set;

public interface FXMiscReporter {
	int getMiscValue();
	@GraphQLInclude("miscModes")
	default @NotNull Set<@NotNull MiscMode> getMiscModes(){ return Modes.getActiveModes(MiscMode.class, getMiscValue()); }
}
